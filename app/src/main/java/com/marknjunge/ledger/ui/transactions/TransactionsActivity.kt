package com.marknjunge.ledger.ui.transactions

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.transactionDetail.TransactionDetailActivity
import com.marknjunge.ledger.utils.*
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.layout_filter_sheet.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*

class TransactionsActivity : BaseActivity() {

    private val transactionsViewModel: TransactionsViewModel by inject()
    private lateinit var transactionsAdapter: PagedTransactionsAdapter
    private val appPreferences: AppPreferences by inject()
    private val REQUEST_WRITE_FILE: Int = 43
    private var csvContent: List<String>? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var startDate: DateTime? = null
    private var endDate: DateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        initializeToolbar()
        initializeBottomSheet()
        setOnClickListeners()
        initializeRecyclerView()
        getMessages()

        showFilterPrompt()
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.menu_export)?.isVisible = true
        menu?.findItem(R.id.menu_filter)?.isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                true
            }
            R.id.menu_export -> {
                exportAsCsv()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_WRITE_FILE && data != null) {
                data.data?.let { uri ->
                    csvContent?.let {
                        val content = it.joinToString("\n").toByteArray()
                        SAFUtils.writeContent(contentResolver, uri, content)

                        Snackbar.make(getRootView(), "Transactions exported", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initializeToolbar() {
        supportActionBar?.title = "Transactions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(filterSheet)
        sheetBehavior.peekHeight = 0
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                viewScrim.alpha = slideOffset * 0.4f
                if (slideOffset > 0.0f) {
                    viewScrim.visibility = View.VISIBLE
                } else {
                    viewScrim.visibility = View.GONE
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    private fun setOnClickListeners() {
        btnClear.setOnClickListener { clearFilters() }
        tvStartDate.setOnClickListener { showStartDatePicker() }
        tvEndDate.setOnClickListener { showEndDatePicker() }

        etSearch.onTextChanged {
            btnClear.visibility = View.VISIBLE
        }

        btnApply.setOnClickListener {
            transactionsViewModel.filter(etSearch.trimmedText, startDate, endDate).observe(this, Observer { items ->
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                transactionsAdapter.submitList(items)
                rvTranasctions.smoothScrollToPosition(0)
                Handler().postDelayed({
                    hideKeyboard()
                }, 500)
            })
        }
    }

    private fun initializeRecyclerView() {
        rvTranasctions.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTranasctions.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        transactionsAdapter = PagedTransactionsAdapter(this) { message ->
            TransactionDetailActivity.start(this, message)
        }
        rvTranasctions.adapter = transactionsAdapter
    }

    private fun getMessages() {
        transactionsViewModel.getPagedMessages().observe(this, Observer { items ->
            transactionsAdapter.submitList(items)
            rvTranasctions.smoothScrollToPosition(0)
        })
    }

    private fun showFilterPrompt() {
        Handler().postDelayed({
            if (!appPreferences.hasSeenFilterPrompt) {
                MaterialTapTargetPrompt.Builder(this)
                        .setTarget(R.id.menu_filter)
                        .setPrimaryText("Filter")
                        .setSecondaryText("You can filter your transactions by text and date")
                        .setIcon(R.drawable.ic_filter)
                        .setBackgroundColour(ContextCompat.getColor(this, R.color.colorActionBarBackground))
                        .setPrimaryTextColour(Color.WHITE)
                        .setSecondaryTextColour(Color.WHITE)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setIconDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)))
                        .setPromptStateChangeListener { _, state ->
                            if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                showExportPrompt()
                            }
                        }
                        .show()

                appPreferences.hasSeenFilterPrompt = true
            } else {
                showExportPrompt()
            }
        }, 1000)
    }

    private fun showExportPrompt() {
        Handler().postDelayed({
            if (!appPreferences.hasSeenExportPrompt) {
                MaterialTapTargetPrompt.Builder(this)
                        .setTarget(R.id.menu_export)
                        .setPrimaryText("Export")
                        .setSecondaryText("You can export your transactions as csv")
                        .setIcon(R.drawable.ic_export)
                        .setBackgroundColour(ContextCompat.getColor(this, R.color.colorActionBarBackground))
                        .setPrimaryTextColour(Color.WHITE)
                        .setSecondaryTextColour(Color.WHITE)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setIconDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)))
                        .show()

                appPreferences.hasSeenExportPrompt = true
            }
        }, 100)
    }

    private fun exportAsCsv() {
        transactionsViewModel.getMessagesForExport().observe(this, Observer { data ->
            csvContent = data
            val filename = "M-Pesa Transactions ${DateTime.now.format("yyyy-MM-dd HH:mm")}.csv"
            val intent = SAFUtils.getIntent("text/csv", filename)
            startActivityForResult(intent, REQUEST_WRITE_FILE)
        })
    }

    private fun showStartDatePicker() {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, { _, y, m, d ->
            val dateTime = DateTime(y, m + 1, d, 0, 0)
            startDate = dateTime
            tvStartDate.text = dateTime.format("dd/MM/yyyy")

            btnClear.visibility = View.VISIBLE
        }, year, month, dayOfMonth)
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.datePicker.minDate = 1173186000000
        dialog.show()
    }

    private fun showEndDatePicker() {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, { _, y, m, d ->
            val dateTime = DateTime(y, m + 1, d, 23, 59, 59)
            if (startDate != null && dateTime.timestamp < startDate!!.timestamp) {
                return@DatePickerDialog
            }
            endDate = dateTime
            tvEndDate.text = dateTime.format("dd/MM/yyyy")

            btnClear.visibility = View.VISIBLE
        }, year, month, dayOfMonth)
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.datePicker.minDate = 1173186000000
        dialog.show()
    }

    private fun clearFilters() {
        etSearch.setText("")
        startDate = null
        tvStartDate.text = "Start Date"
        endDate = null
        tvEndDate.text = "End Date"

        btnClear.visibility = View.GONE
    }
}
