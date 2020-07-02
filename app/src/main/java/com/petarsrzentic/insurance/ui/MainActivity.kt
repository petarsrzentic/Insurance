package com.petarsrzentic.insurance.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.petarsrzentic.insurance.InsuranceRecyclerViewAdapter
import com.petarsrzentic.insurance.InsuranceViewModel
import com.petarsrzentic.insurance.R
import com.petarsrzentic.insurance.data.Insurance
import com.petarsrzentic.insurance.data.InsuranceDao
import com.petarsrzentic.insurance.data.InsuranceRepository
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity(),
    InsuranceRecyclerViewAdapter.TouchEvent {

    private lateinit var insuranceRecyclerViewAdapter: InsuranceRecyclerViewAdapter
    private lateinit var insuranceViewModel: InsuranceViewModel
    private var priceOfInsurance = 0
    private lateinit var checkBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private var sumOfInsurance = arrayListOf<Int>()
        private var sum = 0
        private var suma = 0
        private var suma1 = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        spinner()
        insurance()
        calendar()
        loadData()
    }

    private fun loadData() {
        sharedPreferences = getSharedPreferences("savedSum", Context.MODE_PRIVATE)
        sum = sharedPreferences.getInt("zbir", suma)
        resultText.text = "${getString(R.string.result)}$sum"
        suma1 = sum
    }


    private fun summing() {
        val sumOfInsurance1 = sumOfInsurance
        sum = sumOfInsurance1.sum() + suma1
        suma = sum
        resultText.text = getString(R.string.result)+suma
        saveData()
    }

    private fun saveData() {
        sharedPreferences = getSharedPreferences("savedSum", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putInt("zbir", suma)
        }.apply()
    }


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

    private fun insurance() {
        insuranceImage.setOnClickListener {
            checkBox = findViewById(R.id.editHitno)
            if (checkBox.isChecked) {
                textCheckBox.text = "H1"
                checkBox.isChecked = false
            } else {
                textCheckBox.text = ""
            }
            creator()
        }
    }

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
                        1 -> priceOfInsurance = 130
                        2 -> priceOfInsurance = 250
                        3 -> priceOfInsurance = 400
                        4 -> priceOfInsurance = 600
                        5 -> priceOfInsurance = 800
                        6 -> priceOfInsurance = 1000
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                textSpinner.text = getString(R.string.none)
            }
        }
        val pickInsurance =
            arrayOf(getString(R.string.insurance_category), "cat-1", "cat-2", "cat-3", "cat-4", "cat-5", "cat-6")
        val adapter =
            ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item, pickInsurance
            )
        spinner.adapter = adapter

        insuranceViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(InsuranceViewModel::class.java)

        insuranceRecyclerViewAdapter =
            InsuranceRecyclerViewAdapter(this, this)
        recyclerView.adapter = insuranceRecyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        insuranceViewModel.allInsurance.observe(this, Observer {
            it.let {
                insuranceRecyclerViewAdapter.setItems(it)

            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_database))
                    .setMessage(getString(R.string.do_you_want_to_delete))
                    .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            insuranceViewModel.deleteAll()
                            sharedPreferences.edit().clear().apply()
                            sum = 0
                            resultText.text = "${getString(R.string.result)}$sum"
                        }
                    })
                    .setNegativeButton(getString(R.string.no), object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            return
                        }
                    })
                    .show()
                true
            }
            R.id.action_help -> {
                showHelp()
                true
            }
            R.id.action_backup -> {
//                exportCSV()
                Toast.makeText(this, getString(R.string.under_construction), Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


//    private fun exportCSV(dao: InsuranceDao) {
//    val dbPath = InsuranceRepository(dao).allInsurance
//    Log.i("InsuranceDao", "$dbPath")
//        val dbPath = applicationContext.getDatabasePath("InsuranceDatabase.db")
////        val dataDirectory = InsuranceRepository().allInsurance
//        val dataString = StringBuilder()
//        dataString.append("Date, MSISDN, Category, Hitno, Price of Insurance\n${dbPath.absoluteFile}")
////        dataString.append(dataDirectory)
////
//        try {
//            //saving the file into device
//            val out: FileOutputStream = openFileOutput("data.csv", Context.MODE_PRIVATE)
//            out.write(dataString.toString().toByteArray())
//            out.close()
//            //exporting
////            shareResult()
//            val context: Context = applicationContext
//            val filelocation = File(filesDir, "data.csv")
//            val path: Uri = FileProvider.getUriForFile(
//                context,
//                "com.petarsrzentic.exportdatabase.Fileprovider",
//                filelocation
//            )
//            val fileIntent = Intent(Intent.ACTION_SEND)
//            fileIntent.type = "text/csv"
//            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
//            startActivity(Intent.createChooser(fileIntent, "Send data"))
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//}

    private fun showHelp() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(item: Insurance) {}

    override fun onHold(item: Insurance) {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_import))
            .setPositiveButton(getString(R.string.ok), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    insuranceViewModel.delete(item)
                    sumOfInsurance.remove(priceOfInsurance)
                    insuranceViewModel.update(item)
                    summing()
                }
            }).show()

    }

    private fun creator() {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_new_insurance))
            .setPositiveButton(
                (R.string.ok)
            ) { _, _ ->
                val created = Insurance(
                    0,
                    buttonDate.text.toString(),
                    editMsisdn.text.toString(),
                    textSpinner.text.toString(),
                    textCheckBox.text.toString(),
                    priceOfInsurance
                )
                sumOfInsurance.add(priceOfInsurance)
                summing()
                insuranceViewModel.insert(created)
                insuranceViewModel.update(created)
            }
            .create()
            .show()

    }

}
