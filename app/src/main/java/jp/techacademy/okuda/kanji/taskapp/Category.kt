package jp.techacademy.okuda.kanji.taskapp

import android.util.Log
import java.io.Serializable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent

open class Category : RealmObject(), Serializable {
    var category: String = ""

    @PrimaryKey
    var id: Int = 0

}



