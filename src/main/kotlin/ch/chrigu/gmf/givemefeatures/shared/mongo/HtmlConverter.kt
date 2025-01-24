package ch.chrigu.gmf.givemefeatures.shared.mongo

import ch.chrigu.gmf.givemefeatures.shared.Html
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class HtmlToStringConverter : Converter<Html, String> {
    override fun convert(source: Html): String {
        return source.toString()
    }
}

@ReadingConverter
class StringToHtmlConverter : Converter<String, Html> {
    override fun convert(source: String): Html {
        return Html(source)
    }
}
