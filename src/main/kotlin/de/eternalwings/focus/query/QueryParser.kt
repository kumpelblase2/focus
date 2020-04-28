package de.eternalwings.focus.query

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.parser.Parser

object QueryParser : Grammar<TaskQuery>() {
    private val at by token("@")
    private val hash by token("#")
    private val id by token("[\\w/]+")
    private val colon by token(":")
    private val text by token("\\{[^}]+}")

    private val quotedText by text.map { it.text.substring(1, it.text.length - 1) }
    private val context by (at and id).map { TaskQueryPart.ContextPart(it.t2.text) }
    private val bracketContext by (at and quotedText).map { TaskQueryPart.ContextPart(it.t2) }
    private val project by (hash and id).map { TaskQueryPart.ProjectPart(it.t1.text) }
    private val bracketProject by (hash and quotedText).map { TaskQueryPart.ProjectPart(it.t2) }
    private val shortcut by id.map { TaskQueryPart.ShortcutPart(it.text) }
    private val property by (id and colon and id).map { TaskQueryPart.PropertyPart(it.t1.text, it.t3.text) }
    private val bracketProperty by (id and colon and quotedText).map { TaskQueryPart.PropertyPart(it.t1.text, it.t3) }

    private val part = bracketContext or context or bracketProject or project or bracketProperty or property or shortcut

    override val rootParser: Parser<TaskQuery> = zeroOrMore(part).map { TaskQuery(it) }
}
