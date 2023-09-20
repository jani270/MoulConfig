package io.github.notenoughupdates.moulconfig.platform

import io.github.moulberry.moulconfig.common.IFontRenderer
import io.github.moulberry.moulconfig.common.IItemStack
import io.github.moulberry.moulconfig.common.RenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.InputUtil
import org.lwjgl.opengl.GL11
import java.util.*

class ModernRenderContext(val drawContext: DrawContext) : RenderContext {
    val mouse = MinecraftClient.getInstance().mouse
    val window = MinecraftClient.getInstance().window
    val scissors = Stack<Scissor>()

    data class Scissor(val left: Double, val top: Double, val right: Double, val bottom: Double)

    fun refreshScissors() {
        if (scissors.isEmpty()) {
            GL11.glScissor(0, 0, window.framebufferWidth, window.framebufferHeight)
            return
        }
        var l = 0.0
        var t = 0.0
        var r = window.framebufferWidth * window.scaleFactor
        var b = window.framebufferHeight * window.scaleFactor
        for (frame in scissors) {
            l = l.coerceAtLeast(frame.left * window.scaleFactor)
            t = t.coerceAtLeast(frame.top * window.scaleFactor)
            r = r.coerceAtMost(frame.right * window.scaleFactor)
            b = b.coerceAtMost(frame.bottom * window.scaleFactor)
        }
        GL11.glScissor(l.toInt(), t.toInt(), r.toInt(), b.toInt())
    }

    override fun pushMatrix() {
        drawContext.matrices.push()
    }

    override fun popMatrix() {
        drawContext.matrices.pop()
    }

    override fun translate(x: Float, y: Float, z: Float) {
        drawContext.matrices.translate(x, y, z)
    }

    override fun scale(x: Float, y: Float, z: Float) {
        drawContext.matrices.scale(x, y, z)
    }

    override fun color(r: Float, g: Float, b: Float, a: Float) {
        drawContext.setShaderColor(r, g, b, a)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return when (mouseButton) {
            0 -> {
                mouse.wasLeftButtonClicked()
            }

            2 -> {
                mouse.wasMiddleButtonClicked()
            }

            1 -> {
                mouse.wasRightButtonClicked()
            }

            else -> {
                false
            }
        }
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }

    override fun drawString(
        fontRenderer: IFontRenderer?,
        text: String?,
        x: Int,
        y: Int,
        color: Int,
        shadow: Boolean
    ): Int {
        return drawContext.drawText((fontRenderer as ModernFontRenderer).textRenderer, text, x, y, color, shadow)
    }

    override fun drawColoredRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        drawContext.fill(left.toInt(), top.toInt(), right.toInt(), bottom.toInt(), color)
    }

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float) {
        // TODO: Inverted rect time
    }

    override fun drawTexturedRect(x: Float, y: Float, width: Float, height: Float) {
        drawContext.drawTexture(
            ModernMinecraft.boundTexture,
            x.toInt(),
            y.toInt(),
            width.toInt(),
            height.toInt(),
            width.toInt(),
            height.toInt()
        )
    }

    override fun renderDarkRect(x: Int, y: Int, width: Int, height: Int) {
        drawContext.fill(x, y, x + width, y + height, 0xa0202020.toInt())
        drawContext.drawBorder(x, y, width, height, 0xffc0c0c0.toInt())
    }

    override fun pushScissor(left: Int, top: Int, right: Int, bottom: Int) {
        scissors.add(Scissor(left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble()))
        refreshScissors()
    }

    override fun popScissor() {
        scissors.removeLast()
        refreshScissors()
    }

    override fun renderItemStack(itemStack: IItemStack, x: Int, y: Int, overlayText: String?) {
        val item = (itemStack as ModernItemStack).backing
        drawContext.drawItem(item, x, y)
        drawContext.drawItemInSlot(
            MinecraftClient.getInstance().textRenderer,
            item,
            x,
            y,
            overlayText ?: ""
        )
    }

}
