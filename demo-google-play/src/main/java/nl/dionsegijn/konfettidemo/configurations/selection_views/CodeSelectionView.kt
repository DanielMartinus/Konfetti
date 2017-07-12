package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme
import kotlinx.android.synthetic.main.view_section_code_selection.view.*
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 7/12/17.
 */
@SuppressLint("ViewConstructor")
class CodeSelectionView(context: Context) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_code_selection, this)
        codeView.setOptions(Options.Default.get(context)
                .withLanguage("python")
                .withTheme(ColorTheme.MONOKAI))
    }

}
