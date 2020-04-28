package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import org.jdom2.Element

interface ChangesetElement : Referencable {

    fun toXML(): Element

}
