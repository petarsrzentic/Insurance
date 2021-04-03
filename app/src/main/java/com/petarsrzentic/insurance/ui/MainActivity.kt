package com.petarsrzentic.insurance.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.petarsrzentic.insurance.*
import com.petarsrzentic.insurance.data.Insurance
import com.petarsrzentic.insurance.receiver.AlarmReceiver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rec_dialog.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity(),
    InsuranceRecyclerViewAdapter.TouchEvent {

    private var listOfInsuranceRecords: List<Insurance>? = null
    private var count = 0

    private val storageWritePermission = 101
    private val storageReadPermission = 202

    private lateinit var insuranceRecyclerViewAdapter: InsuranceRecyclerViewAdapter
    private lateinit var insuranceViewModel: InsuranceViewModel
    private lateinit var checkBox: CheckBox
    private var insuranceEntity = Insurance()

    companion object {
        private var totalSumOfInsurances = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        checkBox = findViewById(R.id.editHitno)

        insuranceRecyclerViewAdapter =
            InsuranceRecyclerViewAdapter(this, this)
        recyclerView.adapter = insuranceRecyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        insuranceViewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        insuranceViewModel.allInsurance.observe(this, { listOfInsuranceRecords ->
            // Update the cached copy of the words in the adapter.
            listOfInsuranceRecords?.let {
                run {
                    insuranceRecyclerViewAdapter.setItems(listOfInsuranceRecords)
                    sumListOfInsuranceRecordsFromDatabase(listOfInsuranceRecords)
                    this.listOfInsuranceRecords = listOfInsuranceRecords
                }
            }
        })

        calendar()
        spinner()
        insurance()
        createChannel()

    }

    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.insurance_channelName)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                R.string.insurance_id.toString(), name, importance
            ).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("TAG", "create channel")

        }

    }

    private fun startBroadcasting() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1, intent, 0
        )
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 345600000, pendingIntent)
        Log.d("TAG", "startBroadcasting")
    }

//    private fun checkPermissions(permission: String, requestCode: Int) {
//        when {
//            ContextCompat.checkSelfPermission(
//                applicationContext,
//                permission
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                Toast.makeText(applicationContext, "Permission granted!", Toast.LENGTH_LONG)
//            }
//            shouldShowRequestPermissionRationale(permission) -> showDialog(
//                permission,
//                requestCode
//            )
//
//            else -> ActivityCompat.requestPermissions(
//                this,
//                arrayOf(permission),
//                requestCode
//            )
//        }
//    }

//    private fun showDialog(permission: String, requestCode: Int) {
//        val builder = AlertDialog.Builder(this)
//        builder.apply {
//            setMessage(getString(R.string.permission_required))
//            setTitle(getString(R.string.permission))
//            setPositiveButton("OK") { _, _ ->
//                ActivityCompat.requestPermissions(
//                    this@MainActivity,
//                    arrayOf(permission),
//                    requestCode
//                )
//            }
//        }
//        val dialog = builder.create()
//        dialog.show()
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_LONG)
                    .show()
            }
        }

        when (requestCode) {
            storageWritePermission -> innerCheck("Storage Write")
            storageReadPermission -> innerCheck("Storage Read")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


//    // Load all Insurance records from database and sum them all
//    private fun loadListOfInsuranceRecordsFromDatabase(): List<Insurance> {
//        var listOfInsuranceRecords: List<Insurance>? = null
//        insuranceViewModel = ViewModelProvider.AndroidViewModelFactory(application)
//            .create(InsuranceViewModel::class.java)
//
//        insuranceRecyclerViewAdapter =
//            InsuranceRecyclerViewAdapter(this, this)
//        recyclerView.adapter = insuranceRecyclerViewAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        insuranceViewModel.allInsurance.observe(this, {
//            listOfInsuranceRecords = it
//
//        })
//        return listOfInsuranceRecords!!
//    }

    private fun sumListOfInsuranceRecordsFromDatabase(listOfInsuranceRecords: List<Insurance>) {
        totalSumOfInsurances = 0
        listOfInsuranceRecords.forEach { dbinst -> run { totalSumOfInsurances += dbinst.priceOfInsurance } }
        resultText.text = getString(R.string.result) + totalSumOfInsurances
    }

    // Setup calendar element
    @SuppressLint("SetTextI18n")
    private fun calendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        buttonDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    buttonDate.text = "$dayOfMonth.${month + 1}.$year"
                    insuranceEntity.date = buttonDate.text.toString()
                },
                year,
                month,
                day
            )
            datePicker.show()
            when (month) {
                0 -> monthText.text = getString(R.string.january)
                1 -> monthText.text = getString(R.string.february)
                2 -> monthText.text = getString(R.string.march)
                3 -> monthText.text = getString(R.string.april)
                4 -> monthText.text = getString(R.string.may)
                5 -> monthText.text = getString(R.string.june)
                6 -> monthText.text = getString(R.string.july)
                7 -> monthText.text = getString(R.string.august)
                8 -> monthText.text = getString(R.string.september)
                9 -> monthText.text = getString(R.string.october)
                10 -> monthText.text = getString(R.string.november)
                11 -> monthText.text = getString(R.string.december)
            }
            monthText.visibility = View.VISIBLE
        }

    }

    // Setup insurance category select list
    private fun spinner() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                textSpinner.text = parent?.getItemAtPosition(position).toString().apply {
                    when (position) {
                        1 -> insuranceEntity.priceOfInsurance = 130
                        2 -> insuranceEntity.priceOfInsurance = 250
                        3 -> insuranceEntity.priceOfInsurance = 400
                        4 -> insuranceEntity.priceOfInsurance = 600
                        5 -> insuranceEntity.priceOfInsurance = 800
                        6 -> insuranceEntity.priceOfInsurance = 1000
                    }
                }
                insuranceEntity.category = textSpinner.text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                textSpinner.text = getString(R.string.none)
            }
        }
        val pickInsurance =
            arrayOf(
                getString(R.string.insurance_category),
                "cat-1",
                "cat-2",
                "cat-3",
                "cat-4",
                "cat-5",
                "cat-6"
            )
        val adapter =
            ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item, pickInsurance
            )
        spinner.adapter = adapter

    }

    // Setup add new insurance image click listener
    private fun insurance() {
        insuranceImage.setOnClickListener {
            if (checkBox.isChecked) {
                textCheckBox.text = getString(R.string.h1)
                checkBox.isChecked = false

            } else {
                textCheckBox.text = ""
            }
            insuranceEntity.hitno = textCheckBox.text.toString()
            creator()
        }
    }

    // Create new insurance record
    private fun creator() {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_new_insurance))
            .setPositiveButton(
                (R.string.ok)
            ) { _, _ ->

                insuranceEntity.msisdn = editMsisdn.text.toString()
                if (insuranceEntity.uid != 0) {
                    insuranceViewModel.update(insuranceEntity)
                } else {
                    if (insuranceEntity.date == "") {
                        Toast.makeText(this, getString(R.string.insert_date), Toast.LENGTH_LONG)
                            .show()
                        return@setPositiveButton
                    } else if (insuranceEntity.category == "" || insuranceEntity.category == "Insurance Category") {
                        Toast.makeText(this, getString(R.string.insert_category), Toast.LENGTH_LONG)
                            .show()
                        return@setPositiveButton
                    }
                    insuranceViewModel.insert(insuranceEntity)
                }

            }
            .create()
            .show()
        startBroadcasting()
    }

    // Delete insurance record
    override fun onHold(item: Insurance) {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_import))
            .setPositiveButton(
                getString(R.string.ok)
            ) { _, _ -> insuranceViewModel.delete(item) }.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_database))
                    .setMessage(getString(R.string.do_you_want_to_delete))
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->
                        insuranceViewModel.deleteAll()
                    }
                    .setNegativeButton(
                        getString(R.string.no),
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    .show()
                true
            }
            R.id.action_help -> {
                showHelp()
                true
            }
            R.id.action_backup -> {
                exportToExcel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportToExcel() {

        val context = applicationContext
        val wb = HSSFWorkbook()
        var cell: Cell?
        count++

        //Now we are creating sheet
        val sheetInsurance: Sheet?
        sheetInsurance = wb.createSheet("Table of Insurance")
        //Now column and row
        val rowInsurance = sheetInsurance.createRow(0)

        cell = rowInsurance.createCell(0)
        cell.setCellValue("Date")
        //cell.cellStyle = cellStyle

        cell = rowInsurance.createCell(1)
        cell.setCellValue("MSISDN")
        //cell.cellStyle = cellStyle

        cell = rowInsurance.createCell(2)
        cell.setCellValue("Category")

        cell = rowInsurance.createCell(3)
        cell.setCellValue("HITNO 1")

        cell = rowInsurance.createCell(4)
        cell.setCellValue("Price")

        sheetInsurance.setColumnWidth(0, 10 * 300)
        sheetInsurance.setColumnWidth(1, 10 * 300)
        sheetInsurance.setColumnWidth(2, 10 * 300)
        sheetInsurance.setColumnWidth(3, 10 * 300)
        sheetInsurance.setColumnWidth(4, 10 * 300)

        var rowIndex = 1

        listOfInsuranceRecords?.forEach { insuranceRecord ->
            run {
                val row = sheetInsurance.createRow(rowIndex++)

                var cell: Cell = row.createCell(0)
                cell.setCellValue(insuranceRecord.date)
                //cell.cellStyle = cellStyle

                cell = row.createCell(1)
                cell.setCellValue(insuranceRecord.msisdn)
                //cell.cellStyle = cellStyle

                cell = row.createCell(2)
                cell.setCellValue(insuranceRecord.category)

                cell = row.createCell(3)
                cell.setCellValue(insuranceRecord.hitno)

                cell = row.createCell(4)
                cell.setCellValue(insuranceRecord.priceOfInsurance.toString())
            }
        }

        val file = getFileStreamPath("insurance.xls")
        val outputStream: FileOutputStream? = null

        try {
            val outputStreams = FileOutputStream(file)
            wb.write(outputStreams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
                outputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        try {
            val path = getUriForFile(context, "com.petarsrzentic.insurance.fileprovider", file)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Insurance_$count")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Send mail"))
        } catch (e: Exception) {
            e.message
        }
    }

    // Help file
    private fun showHelp() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(item: Insurance) {
        insuranceEntity = item
        buttonDate.text = insuranceEntity.date
        editMsisdn.setText(insuranceEntity.msisdn)
        insuranceEntity.priceOfInsurance

        spinner.setSelection(1)

        if (insuranceEntity.hitno == "H1") {
            checkBox.isChecked = true
            checkBox.jumpDrawablesToCurrentState()

        } else {
            checkBox.isChecked = false
            checkBox.jumpDrawablesToCurrentState()
        }

        when (insuranceEntity.category) {
            "cat-1" -> spinner.setSelection(1)
            "cat-2" -> spinner.setSelection(2)
            "cat-3" -> spinner.setSelection(3)
            "cat-4" -> spinner.setSelection(4)
            "cat-5" -> spinner.setSelection(5)
            "cat-6" -> spinner.setSelection(6)
        }
    }

}
