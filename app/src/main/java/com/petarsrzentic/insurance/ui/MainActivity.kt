package com.petarsrzentic.insurance.ui

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.petarsrzentic.insurance.InsuranceRecyclerViewAdapter
import com.petarsrzentic.insurance.InsuranceViewModel
import com.petarsrzentic.insurance.R
import com.petarsrzentic.insurance.data.Insurance
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(),
    InsuranceRecyclerViewAdapter.TouchEvent {

    private val filename = "SampleFile.txt"
    private val filepath = "MyFileStorage"
    var myExternalFile: File? = null
    var myData = ""

    private val storageWritePermission = 101
    private val storageReadPermission = 202


    private lateinit var insuranceRecyclerViewAdapter: InsuranceRecyclerViewAdapter
    private lateinit var insuranceViewModel: InsuranceViewModel
    private lateinit var checkBox: CheckBox
    private var insuranceEntity = Insurance()

    companion object {
        private var totalSumOfInsurances = 0
    }

    private val isExternalStorageReadOnly: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
                true
            } else {
                false
            }
        }
    private val isExternalStorageAvailable: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
                true
            } else {
                false
            }
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
        insuranceViewModel.allInsurance.observe(this, Observer { listOfInsuranceRecords ->
            // Update the cached copy of the words in the adapter.
            listOfInsuranceRecords?.let {
                run {
                    insuranceRecyclerViewAdapter.setItems(listOfInsuranceRecords)
                    sumListOfInsuranceRecordsFromDatabase(listOfInsuranceRecords)
                }
            }
        })

        calendar()
        spinner()
        insurance()

    }

    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= M) {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(applicationContext, "Permission granted!", Toast.LENGTH_LONG)
                        .show()
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to access your storage is required to use this application")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, witch ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_LONG)
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

    private fun sumListOfInsuranceRecordsFromDatabase(listOfInsuranceRecords: List<Insurance>) {
        totalSumOfInsurances = 0
        listOfInsuranceRecords.forEach { dbinst -> run { totalSumOfInsurances += dbinst.priceOfInsurance } }
        resultText.text = getString(R.string.result) + totalSumOfInsurances
    }

    // Setup calendar element
    private fun calendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        buttonDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
                textCheckBox.text = "H1"
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
                        Toast.makeText(this, "Insert date", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    } else if (insuranceEntity.category == "" || insuranceEntity.category == "Insurance Category") {
                        Toast.makeText(this, "Insert category", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    insuranceViewModel.insert(insuranceEntity)
                }

            }
            .create()
            .show()

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
                checkPermissions(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    "Write External Storage",
                    storageWritePermission

                )
                generateExcel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun generateExcel() {

        !isExternalStorageAvailable || isExternalStorageReadOnly

        if (isExternalStorageAvailable) {

            if (!isExternalStorageReadOnly) {
                try {
                    myExternalFile = File(getExternalFilesDir(filepath), filename)
                    val fileOutPutStream = FileOutputStream(myExternalFile)
                    fileOutPutStream.write("File text".toByteArray())
                    fileOutPutStream.close()
                    Toast.makeText(applicationContext, "Excel report created", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(applicationContext, "Error creating excel report", Toast.LENGTH_SHORT).show()

                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "External storage is read only",
                    Toast.LENGTH_LONG
                ).show()

            }

        } else {
            Toast.makeText(applicationContext, "External storage not available", Toast.LENGTH_LONG)
                .show()
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
