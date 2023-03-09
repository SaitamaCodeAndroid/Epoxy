package com.airbnb.epoxy.sample.models

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.sample.CarouselData
import com.airbnb.epoxy.sample.R
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks
import com.airbnb.epoxy.sample.views.GridCarouselModel_

class CarouselModelGroup(
    val data: CarouselData,
    callbacks: AdapterCallbacks,
) : EpoxyModelGroup(
    R.layout.model_carousel_group,
    buildModels(
        data,
        callbacks,
    ),
) {
    init {
        id(data.id)
    }

    override fun getSpanSize(
        totalSpanCount: Int,
        position: Int,
        itemCount: Int,
    ): Int {
        return totalSpanCount
    }

    companion object {
        private fun buildModels(
            carousel: CarouselData,
            callbacks: AdapterCallbacks
        ): List<EpoxyModel<*>> {
            val colors = carousel.colors
            val models = ArrayList<EpoxyModel<*>>()

            models.add(ImageButtonModel_()
                .id("add")
                .imageRes(R.drawable.ic_add_circle)
                .clickListener { _, _, _, _ ->
                    callbacks.onAddColorToCarouselClicked(carousel)
                })
            models.add(ImageButtonModel_()
                .id("delete")
                .imageRes(R.drawable.ic_delete)
                .clickListener { _ ->
                    callbacks.onClearCarouselClicked(carousel)
                }
                .show(colors.size > 0))
            models.add(ImageButtonModel_()
                .id("change")
                .imageRes(R.drawable.ic_change)
                .clickListener { _ ->
                    callbacks.onChangeCarouselColorsClicked(carousel)
                }
                .show(colors.size > 0))
            models.add(ImageButtonModel_()
                .id("shuffle")
                .imageRes(R.drawable.ic_shuffle)
                .clickListener { _ ->
                    callbacks.onShuffleCarouselColorsClicked(carousel)
                }
                .show(colors.size > 1))

            val colorModels: MutableList<ColorModel_> = ArrayList()
            for (colorData in colors) {
                colorModels.add(ColorModel_()
                    .id(colorData.id, carousel.id)
                    .color(colorData.colorInt)
                    .playAnimation(colorData.shouldPlayAnimation())
                    .clickListener { _, _, _, position: Int ->
                        // A model click listener is used instead of a normal click listener so that we can get
                        // the current position of the view. Since the view may have been moved when the colors
                        // were shuffled we can't rely on the position of the model when it was added here to
                        // be correct, since the model won't have been rebound when shuffled.
                        callbacks.onColorClicked(carousel, position)
                    })
            }

            models.add(
                GridCarouselModel_()
                    .id("carousel")
                    .models(colorModels)
            )
            return models
        }
    }
}