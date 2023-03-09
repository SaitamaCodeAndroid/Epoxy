package com.airbnb.epoxy.sample

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.epoxy.EpoxyTouchHelper.DragCallbacks
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks
import com.airbnb.epoxy.sample.models.CarouselModelGroup
import java.util.Collections
import java.util.Random

/**
 * Example activity usage for [com.airbnb.epoxy.EpoxyController].
 */
class MainActivity : AppCompatActivity(), AdapterCallbacks {

    private val controller = SampleController(this)
    private var carousels: MutableList<CarouselData>? = ArrayList()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<View>(R.id.recycler_view) as EpoxyRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setController(controller)
        if (savedInstanceState != null) {
            carousels = savedInstanceState.getParcelableArrayList(
                CAROUSEL_DATA_KEY,
                CarouselData::class.java,
            )
        }
        initTouch(recyclerView)
        updateController()
    }

    private fun initTouch(recyclerView: RecyclerView) {
        // Swiping is not used since it interferes with the carousels, but here is an example of
        // how we would set it up.

//    EpoxyTouchHelper.initSwiping(recyclerView)
//        .leftAndRight()
//        .withTarget(CarouselModelGroup.class)
//        .andCallbacks(new SwipeCallbacks<CarouselModelGroup>() {
//
//          @Override
//          public void onSwipeProgressChanged(CarouselModelGroup model, View itemView,
//              float swipeProgress) {
//
        // Fades a background color in the further you swipe. A different color is used
        // for swiping left vs right.
//            int alpha = (int) (Math.abs(swipeProgress) * 255);
//            if (swipeProgress > 0) {
//              itemView.setBackgroundColor(Color.argb(alpha, 0, 255, 0));
//            } else {
//              itemView.setBackgroundColor(Color.argb(alpha, 255, 0, 0));
//            }
//          }
//
//          @Override
//          public void onSwipeCompleted(CarouselModelGroup model, View itemView, int position,
//              int direction) {
//            carousels.remove(model.data);
//            updateController();
//          }
//
//          @Override
//          public void clearView(CarouselModelGroup model, View itemView) {
//            itemView.setBackgroundColor(Color.WHITE);
//          }
//        });
        EpoxyTouchHelper.initDragging(controller)
            .withRecyclerView(recyclerView)
            .forVerticalList()
            .withTarget(CarouselModelGroup::class.java)
            .andCallbacks(object : DragCallbacks<CarouselModelGroup?>() {
                @ColorInt
                val selectedBackgroundColor = Color.argb(200, 200, 200, 200)
                var backgroundAnimator: ValueAnimator? = null

                override fun onDragStarted(
                    model: CarouselModelGroup?,
                    itemView: View,
                    adapterPosition: Int
                ) {
                    backgroundAnimator = ValueAnimator
                        .ofObject(ArgbEvaluator(), Color.WHITE, selectedBackgroundColor)
                    backgroundAnimator?.addUpdateListener { animator: ValueAnimator ->
                        itemView.setBackgroundColor(
                            animator.animatedValue as Int
                        )
                    }
                    backgroundAnimator?.start()
                    itemView
                        .animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                }

                override fun onDragReleased(
                    model: CarouselModelGroup?,
                    itemView: View,
                ) {
                    if (backgroundAnimator != null) {
                        backgroundAnimator!!.cancel()
                    }
                    backgroundAnimator = ValueAnimator.ofObject(
                        ArgbEvaluator(), (itemView.background as ColorDrawable).color,
                        Color.WHITE
                    )
                    backgroundAnimator?.addUpdateListener { animator: ValueAnimator ->
                        itemView.setBackgroundColor(
                            animator.animatedValue as Int
                        )
                    }
                    backgroundAnimator?.start()
                    itemView
                        .animate()
                        .scaleX(1f)
                        .scaleY(1f)
                }

                override fun clearView(
                    model: CarouselModelGroup?,
                    itemView: View
                ) {
                    onDragReleased(model, itemView)
                }

                override fun onModelMoved(
                    fromPosition: Int,
                    toPosition: Int,
                    modelBeingMoved: CarouselModelGroup?,
                    itemView: View?
                ) {
                    val carouselIndex = carousels!!.indexOf(modelBeingMoved?.data)
                    carousels?.add(
                            carouselIndex + (toPosition - fromPosition),
                            carousels!!.removeAt(carouselIndex)
                        )
                }
            })
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putParcelableArrayList(CAROUSEL_DATA_KEY, carousels as ArrayList<out Parcelable?>?)
        controller.onSaveInstanceState(state)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        controller.onRestoreInstanceState(savedInstanceState)
    }

    private fun updateController() {
        carousels?.let {
            controller.setData(it.toList())
        }
    }

    override fun onAddCarouselClicked() {
        val carousel = CarouselData(
            carousels!!.size.toLong(),
            ArrayList(),
        )
        addColorToCarousel(carousel)
        carousels!!.add(0, carousel)
        updateController()
    }

    private fun addColorToCarousel(carousel: CarouselData?) {
        val colors = carousel!!.colors
        colors.add(0, ColorData(randomColor(), colors.size.toLong()))
    }

    override fun onClearCarouselsClicked() {
        carousels!!.clear()
        updateController()
    }

    override fun onShuffleCarouselsClicked() {
        carousels?.shuffle()
        updateController()
    }

    override fun onChangeAllColorsClicked() {
        for (carouselData in carousels!!) {
            for (colorData in carouselData.colors) {
                colorData.colorInt = randomColor()
            }
        }
        updateController()
    }

    override fun onAddColorToCarouselClicked(carousel: CarouselData?) {
        addColorToCarousel(carousel)
        updateController()
    }

    override fun onClearCarouselClicked(carousel: CarouselData?) {
        carousel!!.colors.clear()
        updateController()
    }

    override fun onShuffleCarouselColorsClicked(carousel: CarouselData?) {
        carousel?.colors?.shuffle()
        updateController()
    }

    override fun onChangeCarouselColorsClicked(carousel: CarouselData?) {
        for (colorData in carousel!!.colors) {
            colorData.colorInt = randomColor()
        }
        updateController()
    }

    override fun onColorClicked(carousel: CarouselData?, colorPosition: Int) {
        val carouselPosition = carousels!!.indexOf(carousel)
        val colorData = carousels!![carouselPosition].colors[colorPosition]
        colorData.setPlayAnimation(!colorData.shouldPlayAnimation())
        updateController()
    }

    companion object {
        private val RANDOM = Random()
        private const val CAROUSEL_DATA_KEY = "carousel_data_key"
        private fun randomColor(): Int {
            val r = RANDOM.nextInt(256)
            val g = RANDOM.nextInt(256)
            val b = RANDOM.nextInt(256)
            return Color.rgb(r, g, b)
        }
    }
}