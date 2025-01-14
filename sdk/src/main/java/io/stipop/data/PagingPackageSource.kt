package io.stipop.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.stipop.Stipop
import io.stipop.api.StipopApi
import io.stipop.models.StickerPackage
import io.stipop.models.StipopApiEnum
import io.stipop.models.response.StickerPackagesResponse
import retrofit2.HttpException

internal class PagingPackageSource(
    private val query: String? = null,
    private val newOrder: Boolean = false
) : PagingSource<Int, StickerPackage>() {

    private val STARTING_PAGE_INDEX = 1
    private var currentQuery: String? = null
    private var currentOrder: Boolean = false

    override fun getRefreshKey(state: PagingState<Int, StickerPackage>): Int? {
        if (query != currentQuery) {
            currentQuery = query
            return null
        }
        if (currentOrder != newOrder) {
            currentOrder = newOrder
            return null
        }
        return state.anchorPosition?.let { anchorPosition -> state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1) }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StickerPackage> {
        val pageNumber = params.key ?: STARTING_PAGE_INDEX
        val userId = Stipop.userId
        val limit = 20
        return try {
            var response: StickerPackagesResponse? = null
            when (newOrder) {
                true -> {
                    try {
                        response = StipopApi.create().getNewStickerPackages(
                            userId = userId,
                            limit = limit,
                            pageNumber = pageNumber,
                            countryCode = Stipop.countryCode,
                            lang = Stipop.lang,
                            query = query
                        )
                    } catch(exception: HttpException){
                        when(exception.code()){
                            401 -> Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_NEW_STICKER_PACKAGES, exception)
                        }
                        return LoadResult.Error(exception)
                    }
                }
                false -> {
                    try {
                        response = StipopApi.create().getTrendingStickerPackages(
                            userId = userId,
                            limit = limit,
                            pageNumber = pageNumber,
                            countryCode = Stipop.countryCode,
                            lang = Stipop.lang,
                            query = query
                        )
                    } catch(exception: HttpException){
                        when(exception.code()){
                            401 -> {
                                Stipop.sAuthDelegate?.httpException(StipopApiEnum.GET_TRENDING_STICKER_PACKAGES, exception)
                            }
                        }
                        return LoadResult.Error(exception)
                    }
                }
            }
            val stickerPackages = response.body?.packageList ?: emptyList()
            val nextKey = if (stickerPackages.isNullOrEmpty()) {
                null
            } else {
                pageNumber + 1
            }
            LoadResult.Page(
                data = stickerPackages,
                prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = nextKey
            )
        } catch(exception: Exception){
            Stipop.trackError(exception)
            return LoadResult.Error(exception)
        }
    }
}