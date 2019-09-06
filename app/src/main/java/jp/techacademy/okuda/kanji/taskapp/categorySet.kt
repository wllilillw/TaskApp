package jp.techacademy.okuda.kanji.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import android.util.Log
import kotlinx.android.synthetic.main.activity_category.*

class categorySet : AppCompatActivity() {

    private var mCategory: Category? = null
    private val mOnCategoryClickListener = View.OnClickListener {
        addCategory2()
    }
    var categoryList: MutableList<String> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_set)



/*        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }*/

        buttonTouroku.setOnClickListener(mOnCategoryClickListener)

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

        val category = textCategorySet.text.toString()

        mCategory!!.category = category

        realm.copyToRealmOrUpdate(mCategory)
        realm.commitTransaction()

        categoryList.add(category)
        Log.d("aaa",category)


        realm.close()
        finish()
    }



}