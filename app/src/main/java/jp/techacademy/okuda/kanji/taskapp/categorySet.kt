package jp.techacademy.okuda.kanji.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import android.util.Log
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_category.*

class categorySet : AppCompatActivity() {

    private var mCategory: Category? = null
    private val mOnCategoryClickListener = View.OnClickListener {
        addCategory2()
    }
    //var categoryList: MutableList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_set)

        buttonTouroku.setOnClickListener(mOnCategoryClickListener)

    }

    private fun addCategory2() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val category = textCategorySet.text.toString()
        val categorySearchResult = realm.where(Category::class.java).equalTo("category", category).findAll()
        val categoryResultText=categorySearchResult.toString()


        if (mCategory == null) {
            // 新規作成の場合
            mCategory = Category()

            val categoryRealmResults2 = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (categoryRealmResults2.max("id") != null) {
                    categoryRealmResults2.max("id")!!.toInt()+1
                } else {
                    0
                }
            mCategory!!.id = identifier
        }
            if (categorySearchResult.size != 0){
                realm.close()
                finish()
                return
            }

            mCategory!!.category = category

            realm.copyToRealmOrUpdate(mCategory!!)
            realm.commitTransaction()
            //}

            realm.close()
            finish()
        //}

    }

    }

