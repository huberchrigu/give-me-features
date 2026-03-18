package ch.chrigu.gmf.plugins

typealias FormValues = Map<String, List<String>>

fun FormValues.string(key: String) = get(key)?.firstOrNull() ?: ""
fun FormValues.boolean(key: String) = get(key)?.firstOrNull() == "on"