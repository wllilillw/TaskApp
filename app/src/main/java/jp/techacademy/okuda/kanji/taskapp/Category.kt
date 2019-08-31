package jp.techacademy.okuda.kanji.taskapp

import android.renderscript.Sampler
import java.io.Serializable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Category : RealmObject(), Serializable {
    var category :String = ""

    @PrimaryKey
    var id: Int = 0
}
