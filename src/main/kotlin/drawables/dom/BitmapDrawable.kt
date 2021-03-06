package drawables.dom

import IconPreviewFactory
import com.intellij.util.ui.UIUtil
import drawables.Utils
import org.w3c.dom.Element
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon


class BitmapDrawable : Drawable() {

    companion object {
        private const val SRC = "android:src"
        private const val TINT = "android:tint"
    }

    private var icon: Icon? = null
    private var tintColor: Color? = null

    override fun inflate(element: Element) {
        super.inflate(element)
        element.getAttribute(SRC)?.let { Utils.getPsiFileFromPath(it) }
                ?.run { icon = IconPreviewFactory.createIconInner(this) }
        element.getAttribute(TINT)?.run { Utils.parseAttributeAsColor(this, tintColor) }?.also { tintColor = it }
    }


    override fun draw(image: BufferedImage) {
        super.draw(image)
        icon?.also {
            if (it is ImageIcon) {

                val width = it.image.getWidth(null)
                val height = it.image.getHeight(null)
                val dyed = UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
                val graphics = dyed.createGraphics()
                graphics.drawImage(it.image, 0, 0, null)
                tintColor?.also {
                    graphics.composite = AlphaComposite.SrcAtop
                    graphics.color = tintColor
                    graphics.fillRect(0, 0, width, height)
                }
                graphics.dispose()

                Utils.drawResizedIcon(dyed, image)
            }
        }
    }
}