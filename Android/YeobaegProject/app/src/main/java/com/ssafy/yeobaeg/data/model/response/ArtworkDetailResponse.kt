package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.data.model.request.Image
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.art.ExhibitionSpot
import kotlinx.serialization.Serializable

@Serializable
data class ArtworkDetailResponse(
    val artwork: ArtworkDetailData
)
@Serializable
data class ArtworkDetailData(
    val artist: Artist,
    val artworkId: Long,
    val bookmarkCount: Int,
    val category: String,
    val currentExhibition: CurrentExhibition,
    val description: String,
    val heightCm: Int,
    val images: List<Image>,
    val isBookmarked: Boolean,
    val price: Int,
    val productionYear: Int,
    val status: String,
    val title: String,
    val viewCount: Int,
    val weightKg: Double,
    val widthCm: Int
)

@Serializable
data class CurrentExhibition(
    val cafeAddress: String,
    val cafeId: Long,
    val cafeImageUrl: String,
    val cafeName: String,
    val exhibitionEndDate: String,
    val exhibitionStartDate: String,
    val slotName: String
)

fun ArtworkDetailData.toDomain(): ArtworkDetail = ArtworkDetail(
    id = artworkId,
    title = title,
    category = category,
    price = price,
    widthCm = widthCm,
    heightCm = heightCm,
    productionYear = productionYear,
    weightKg = weightKg,
    description = description,
    viewCount = viewCount,
    bookmarkCount = bookmarkCount,
    isBookmarked = isBookmarked,
    images = images.map { it.imageUrl },
    artist = ArtistSummary(
        artistId = artist.userId,
        artistName = artist.artistName,
        introduction = artist.introduction,
        profileImageUrl = artist.profileImageUrl,
        artworkCount = 0
    ),
    exhibition = currentExhibition.let {
        ExhibitionSpot(
            cafeId = it.cafeId,
            cafeName = it.cafeName,
            cafeAddress = it.cafeAddress,
            cafeImageUrl = it.cafeImageUrl,
            slotName = it.slotName,
            startDate = it.exhibitionStartDate,
            endDate = it.exhibitionEndDate
        )
    }
)