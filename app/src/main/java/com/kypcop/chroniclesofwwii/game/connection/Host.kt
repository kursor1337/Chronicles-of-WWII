package com.kypcop.chroniclesofwwii.game.connection

import android.net.nsd.NsdServiceInfo
import android.os.Parcel
import android.os.Parcelable
import com.kypcop.chroniclesofwwii.game.Const
import java.net.InetAddress
import java.net.UnknownHostException

class Host(val name: String, val inetAddress: InetAddress, val port: Int) : Parcelable {

    constructor(nsdServiceInfo: NsdServiceInfo) : this(
            remakeName(nsdServiceInfo),
            nsdServiceInfo.host,
            nsdServiceInfo.port
    )

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: DUMMY,
            getInetAddressFrom(parcel) ?: LOOPBACK_ADDRESS,
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(inetAddress.address.size)
        parcel.writeByteArray(inetAddress.address)
        parcel.writeInt(port)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Host> {
        const val DUMMY = "dummy"
        val LOOPBACK_ADDRESS: InetAddress = InetAddress.getLoopbackAddress()
        const val FILTER_TEXT = ""
        const val JSON_NAME = "name"
        const val JSON_FILTER_TEXT = "filterText"

        override fun createFromParcel(parcel: Parcel): Host {
            return Host(parcel)
        }

        override fun newArray(size: Int): Array<Host?> {
            return arrayOfNulls(size)
        }

        fun getInetAddressFrom(parcel: Parcel): InetAddress? {
            val addressBytes = ByteArray(parcel.readInt())
            parcel.readByteArray(addressBytes)
            return try {
                InetAddress.getByAddress(addressBytes)
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                null
            }
        }

        fun remakeName(serviceInfo: NsdServiceInfo) : String {
            return serviceInfo.serviceName.substring(serviceInfo.serviceName.indexOf('.') + 1)
        }

    }
}