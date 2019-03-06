package com.example.keeper

import android.os.Parcel
import android.os.Parcelable
import org.litepal.crud.LitePalSupport
import java.util.*
import java.util.Calendar.*


class BillItem(
    var id: Long = 0,
    var price: Float = 0F,
    var type: Int = PAYOUT,
    var remark: String? = null,
    var category: String? = null,
    var year: Int = 0,
    var month: Int = 0,
    var day: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var timeMills: Long = 0
) : LitePalSupport(), Parcelable, Comparable<BillItem> {

    val isPayout get() = this.type == PAYOUT
    val isIncome get() = this.type == INCOME

    init {
        this.price = 0F
        this.type = 0
        this.remark = ""
        this.category = "消费"
        this.setTime(Calendar.getInstance().timeInMillis)
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        price = parcel.readFloat()
        type = parcel.readInt()
        remark = parcel.readString()
        category = parcel.readString()
        year = parcel.readInt()
        month = parcel.readInt()
        day = parcel.readInt()
        hour = parcel.readInt()
        minute = parcel.readInt()
        timeMills = parcel.readLong()
    }

    override fun compareTo(other: BillItem): Int {
        return other.timeMills.compareTo(this.timeMills)
    }

    fun setTime(timeMills: Long) {
        val c = Calendar.getInstance()
        c.timeInMillis = timeMills
        this.timeMills = timeMills
        this.year = c.get(YEAR)
        this.month = c.get(MONTH) + 1
        this.day = c.get(DAY_OF_MONTH)
        this.hour = c.get(HOUR_OF_DAY)
        this.minute = c.get(MINUTE)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeFloat(price)
        parcel.writeInt(type)
        parcel.writeString(remark)
        parcel.writeString(category)
        parcel.writeInt(year)
        parcel.writeInt(month)
        parcel.writeInt(day)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeLong(timeMills)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillItem> {
        const val PAYOUT = 0
        const val INCOME = 1
        override fun createFromParcel(parcel: Parcel): BillItem {
            return BillItem(parcel)
        }

        override fun newArray(size: Int): Array<BillItem?> {
            return arrayOfNulls(size)
        }
    }

}