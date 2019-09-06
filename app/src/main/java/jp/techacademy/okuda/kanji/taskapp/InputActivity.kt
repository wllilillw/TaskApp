package jp.techacademy.okuda.kanji.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.AdapterView
import android.widget.Spinner
import io.realm.RealmChangeListener
import android.widget.ArrayAdapter
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class InputActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            setCategoryList()
        }
    }

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mTask: Task? = null
    private var mCategory: Category? = Category()
    private var mcategorySet=categorySet()
    private var spinnerItems :MutableList<String> = arrayListOf()

    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                date_button.text = dateString
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private val mOnTimeClickListener = View.OnClickListener {
        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                mHour = hour
                mMinute = minute
                val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
                times_button.text = timeString
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    private val mOnDoneClickListener = View.OnClickListener {
        addTask()
        finish()
    }
    private val mOnCategoryClickListener = View.OnClickListener {
        addCategory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)



        // UI部品の設定
        date_button.setOnClickListener(mOnDateClickListener)
        times_button.setOnClickListener(mOnTimeClickListener)
        done_button.setOnClickListener(mOnDoneClickListener)
        category_button.setOnClickListener(mOnCategoryClickListener)

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        val intent = intent
        val taskId = intent.getIntExtra(EXTRA_TASK, -1)
        val categoryId = intent.getIntExtra(EXTRA_CATEGORY, -1)

        val realm = Realm.getDefaultInstance()
        mTask = realm.where(Task::class.java).equalTo("id", taskId).findFirst()
        mCategory = realm.where(Category::class.java).equalTo("id", categoryId).findFirst()
        //val categoryRealmResults = realm.where(Category::class.java).findAll()
        realm.close()


        // Kotlin Android Extensions
        if (mCategory == null){
            category_spinner_textView.text="カテゴリーが登録されていません"
        }else{
            category_spinner_textView.text="カテゴリーを選択"
        }


        // ActionBarを設定する
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }


        if (mTask == null) {
            // 新規作成の場合
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        } else {
            // 更新の場合
            title_edit_text.setText(mTask!!.title)
            content_edit_text.setText(mTask!!.contents)

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            date_button.text = dateString
            times_button.text = timeString
        }
    }

    private fun addCategory(){
        val intent = Intent(this, categorySet::class.java)
        startActivity(intent)
    }

    private fun addTask() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mTask == null) {
            // 新規作成の場合
            mTask = Task()

            val taskRealmResults = realm.where(Task::class.java).findAll()
            val categoryRealmResult=realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            val identifierCategory: Int =
                if (categoryRealmResult.max("id") != null) {    //might be 0
                    categoryRealmResult.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mTask!!.id = identifier
            mTask!!.mCategory!!.id=identifierCategory
        }

        val title = title_edit_text.text.toString()
        val content = content_edit_text.text.toString()

        mTask!!.title = title
        mTask!!.contents = content




        // リスナーを登録
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                val realm = Realm.getDefaultInstance()
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as String
                // Kotlin Android Extensions
                Log.d("aaa",item)
                mTask!!.mCategory!!.category = item

            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }




        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mTask!!.date = date

        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()

        val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
        resultIntent.putExtra(EXTRA_TASK, mTask!!.id)
        val resultPendingIntent = PendingIntent.getBroadcast(
            this,
            mTask!!.id,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, resultPendingIntent)
    }

    private fun setCategoryList() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val categoryRealmResults = mRealm.where(Category::class.java).findAll()
        //val results = mRealm.where(Category::class.java).equalTo("category", mCategory!!.id).findAll()
        if (categoryRealmResults != null) {


            val loopEnd = categoryRealmResults.max("id")!!.toInt()


            var i: Int = 0
            while (i < loopEnd) {
                //Log.d("aaax", mTask!!.mCategory!!.category)
                spinnerItems.add(categoryRealmResults[i]!!.category)
                i += 1
            }
        }
            Log.d("aaass", spinnerItems.toString())

            //return spinnerItems


    }
    override fun onResume() {
        super.onResume()

        setCategoryList()


        // ArrayAdapter
        val adapter = ArrayAdapter(applicationContext,
            android.R.layout.simple_spinner_item, spinnerItems)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // spinner に adapter をセット
        category_spinner.adapter = adapter
    }

}