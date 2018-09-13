package edu.artic.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import edu.artic.db.models.ArticObject
import io.reactivex.Flowable

@Dao
interface ArticObjectDao {
    @Query("select * from ArticObject limit 1")
    fun getFirstObject(): ArticObject


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addObjects(objects: List<ArticObject>)

    //Must be flowable due to need to use this in 2 places
    @Query("select * from ArticObject where nid = :id")
    fun getObjectById(id: String): Flowable<ArticObject>


    //Must be flowable due to need to use this in 2 places
    @Query("select * from ArticObject where nid = :id")
    fun getObjectByIdSyncronous(id: String): ArticObject?

    /**
     * Return the object with an audio file given by this number.
     *
     * We should migrate to [ArticObject.objectSelectorNumbers] in future to
     * support objects with multiple top-level audio tracks.
     *
     * Not expected to be used much outside the 'audio' module.
     */
    @Query("select * from ArticObject where objectSelectorNumber = :selectorNumber")
    fun getObjectBySelectorNumber(selectorNumber: String): ArticObject?

    /**
     * Retrieves all of the [ArticObject]s found in a specific
     * gallery. May be an empty list if none claim to belong to
     * the given gallery.
     *
     * @see ArticObject.galleryLocation
     * @see edu.artic.db.models.ArticGallery.floor
     */
    @Query("select * from ArticObject where galleryLocation in (:galleryTitle)")
    fun getObjectsInGallery(galleryTitle: String): List<ArticObject>

    @Query(value = "select * from ArticObject where nid in (:ids)")
    fun getObjectsByIdList(ids: List<String>): Flowable<List<ArticObject>>

    @Query("select * from ArticObject where floor = :floor")
    fun getObjectsByFloor(floor: Int): Flowable<List<ArticObject>>
}