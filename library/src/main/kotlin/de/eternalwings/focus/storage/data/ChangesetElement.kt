package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable

/**
 * An element that can appear at the root of a changeset. Every element
 * at this level has to be [Referencable], too. Any changeset element
 * denotes a change of the omnifocus store or serves as reference to
 * other changeset elements.
 */
interface ChangesetElement : Referencable {

}
