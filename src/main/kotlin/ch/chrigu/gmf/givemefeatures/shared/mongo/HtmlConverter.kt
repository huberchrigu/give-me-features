package ch.chrigu.gmf.givemefeatures.shared.mongo

import ch.chrigu.gmf.givemefeatures.shared.Markdown
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class HtmlToStringConverter : Converter<Markdown, String> {
    override fun convert(source: Markdown): String {
        return source.toString()
    }
}

@ReadingConverter
class StringToHtmlConverter : Converter<String, Markdown> {
    override fun convert(source: String): Markdown {
        return Markdown(source)
    }
}
