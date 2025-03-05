package ie.setu.placemark.adapters

import ie.setu.placemark.models.PlacemarkModel

interface PlacemarkListener {
    fun onPlacemarkClick(placemark: PlacemarkModel)
}
