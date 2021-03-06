package edu.artic.map.rendering

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.model.Tile
import edu.artic.db.models.ArticMapFloor
import edu.artic.image.GlideApp
import timber.log.Timber
import java.io.IOException
import kotlin.math.pow

/**
 * Description:
 */
class GlideMapTileProvider(private val context: Context,
                           private val floor: ArticMapFloor) : BaseMapTileProvider() {

    val defaultTile = Tile(
            TILE_SIZE.toInt(),
            TILE_SIZE.toInt(),
            context.assets.open("tiles/blank-tile.jpg").readBytes())

    override fun getAdjustedTile(x: Int, y: Int, zoom: Int): Tile? {
        return when {
            x >= 0 && y >= 0 -> {
                // calculate tile row count
                val tileXCount = (2.0).pow(zoom)

                // grab which tile based on row, what y number it is in the tile list, x position
                val tileNumber = (y * tileXCount + x).toInt()

                val path = "${floor.tiles}zoom$zoom/tiles-$tileNumber.jpg"
                return try {

                    /**
                     * Skipping the glide cache.
                     * OkHttp will handle the work of caching the image.
                     * @See edu.artic.image.GlideModule
                     */
                    val array = GlideApp.with(context)
                            .`as`(ByteArray::class.java)
                            .load(path)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .submit(TILE_SIZE.toInt(), TILE_SIZE.toInt())
                            .get()
                    Tile(TILE_SIZE.toInt(),
                            TILE_SIZE.toInt(),
                            array
                    )
                } catch (e: IOException) {
                    Timber.e("Could not find tile with $path")
                    null
                }
            }
            else -> {
                defaultTile
            }
        }
    }

}
