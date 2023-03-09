package com.airbnb.epoxy.sample

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt

open class ColorData : Parcelable {
    val id: Long

    @get:ColorInt
    @ColorInt
    var colorInt: Int
    private var playAnimation = false

    constructor(colorInt: Int, id: Long) {
        this.colorInt = colorInt
        this.id = id
    }

    fun setPlayAnimation(playAnimation: Boolean) {
        this.playAnimation = playAnimation
    }

    fun shouldPlayAnimation(): Boolean {
        return playAnimation
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(colorInt)
        dest.writeLong(id)
    }

    protected constructor(`in`: Parcel) {
        colorInt = `in`.readInt()
        id = `in`.readLong()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ColorData?> = object : Parcelable.Creator<ColorData?> {
            override fun createFromParcel(source: Parcel): ColorData {
                return ColorData(source)
            }

            override fun newArray(size: Int): Array<ColorData?> {
                return arrayOfNulls(size)
            }
        }
    }
}