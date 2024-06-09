package com.example.wefly.model

import android.os.Parcel
import android.os.Parcelable

// edit "tipoViaggio" and "partecipanti"

// Data displayed in the recycler view [titoloImmagine, titoloViaggio, citta, dataPartenza, budget]

// delete titoloImmagine, tipoViaggio, Affinita
// add partecipanti max

data class ModelElencoViaggi(var vid: String, var imageUrl : String, var titoloViaggio: String, var budget: String, var nazione : String, var citta : String, var dataPartenza : String, var dataRitorno: String, var partecipanti : Int, var partecipantiMax : Int, var partecipantiStr: String, var descrizione : String, var scelta1: Boolean, var scelta2: Boolean, var scelta3: Boolean, var scelta4: Boolean, var scelta5: Boolean, var scelta6: Boolean, var scelta7: Boolean, var scelta8: Boolean, var scelta9: Boolean, var scelta10: Boolean): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vid)
        parcel.writeString(imageUrl)
        parcel.writeString(titoloViaggio)
        parcel.writeString(budget)
        parcel.writeString(nazione)
        parcel.writeString(citta)
        parcel.writeString(dataPartenza)
        parcel.writeString(dataRitorno)
        parcel.writeInt(partecipanti)
        parcel.writeInt(partecipantiMax)
        parcel.writeString(partecipantiStr)
        parcel.writeString(descrizione)
        parcel.writeByte(if (scelta1) 1 else 0)
        parcel.writeByte(if (scelta2) 1 else 0)
        parcel.writeByte(if (scelta3) 1 else 0)
        parcel.writeByte(if (scelta4) 1 else 0)
        parcel.writeByte(if (scelta5) 1 else 0)
        parcel.writeByte(if (scelta6) 1 else 0)
        parcel.writeByte(if (scelta7) 1 else 0)
        parcel.writeByte(if (scelta8) 1 else 0)
        parcel.writeByte(if (scelta9) 1 else 0)
        parcel.writeByte(if (scelta10) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelElencoViaggi> {
        override fun createFromParcel(parcel: Parcel): ModelElencoViaggi {
            return ModelElencoViaggi(parcel)
        }

        override fun newArray(size: Int): Array<ModelElencoViaggi?> {
            return arrayOfNulls(size)
        }
    }
}