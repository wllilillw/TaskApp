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
import kotlinx.android.synthetic.main.activity_category.*

class categorySet : AppCompatActivity() {
    private var mCategory: Category? = null
    private val mOnCategoryClickListener = View.OnClickListener {
        addCategory2()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_set)

/*        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }*/

        buttonTouroku.setOnClickListener(mOnCategoryClickListener)
        val intent = intent
        val categoryId = intent.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        mCategory = realm.where(Category::class.java).equalTo("id", categoryId).findFirst()
        realm.close()
    }

    private fun addCategory2() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        if (mCategory == null) {
            // 新規作成の場合
            mCategory = Category()

            val categoryRealmResults = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (categoryRealmResults.max("id") != null) {
                    categoryRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mCategory!!.id = identifier
        }

        val category = textCategorySet.toString()

        mCategory!!.category = category

        realm.copyToRealmOrUpdate(mCategory)
        realm.commitTransaction()
        Log.d("aaa",category)
        Log.d("aaa",mCategory!!.id.toString())

        realm.close()
    }


}