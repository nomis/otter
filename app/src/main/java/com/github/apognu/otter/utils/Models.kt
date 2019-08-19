package com.github.apognu.otter.utils

import com.preference.PowerPreference

sealed class CacheItem<D : Any>(val data: List<D>)
class ArtistsCache(data: List<Artist>) : CacheItem<Artist>(data)
class AlbumsCache(data: List<Album>) : CacheItem<Album>(data)
class TracksCache(data: List<Track>) : CacheItem<Track>(data)
class PlaylistsCache(data: List<Playlist>) : CacheItem<Playlist>(data)
class PlaylistTracksCache(data: List<PlaylistTrack>) : CacheItem<PlaylistTrack>(data)
class FavoritesCache(data: List<Favorite>) : CacheItem<Favorite>(data)
class QueueCache(data: List<Track>) : CacheItem<Track>(data)

abstract class FunkwhaleResponse<D : Any> {
  abstract val count: Int
  abstract val next: String?

  abstract fun getData(): List<D>
}

data class ArtistsResponse(override val count: Int, override val next: String?, val results: List<Artist>) : FunkwhaleResponse<Artist>() {
  override fun getData() = results
}

data class AlbumsResponse(override val count: Int, override val next: String?, val results: AlbumList) : FunkwhaleResponse<Album>() {
  override fun getData() = results
}

data class TracksResponse(override val count: Int, override val next: String?, val results: List<Track>) : FunkwhaleResponse<Track>() {
  override fun getData() = results
}

data class FavoritesResponse(override val count: Int, override val next: String?, val results: List<Favorite>) : FunkwhaleResponse<Favorite>() {
  override fun getData() = results
}

data class PlaylistsResponse(override val count: Int, override val next: String?, val results: List<Playlist>) : FunkwhaleResponse<Playlist>() {
  override fun getData() = results
}

data class PlaylistTracksResponse(override val count: Int, override val next: String?, val results: List<PlaylistTrack>) : FunkwhaleResponse<PlaylistTrack>() {
  override fun getData() = results
}

data class Covers(val original: String)

typealias AlbumList = List<Album>

data class Album(
  val id: Int,
  val artist: Artist,
  val title: String,
  val cover: Covers
) {
  data class Artist(val name: String)
}

data class Artist(
  val id: Int,
  val name: String,
  val albums: List<Album>?
) {
  data class Album(
    val title: String,
    val cover: Covers
  )
}

data class Track(
  val id: Int,
  val title: String,
  val artist: Artist,
  val album: Album,
  val uploads: List<Upload>
) {
  var current: Boolean = false
  var favorite: Boolean = false

  data class Upload(
    val listen_url: String,
    val duration: Int,
    val bitrate: Int
  )

  override fun equals(other: Any?): Boolean {
    return when (other) {
      is Track -> other.id == id
      else -> false
    }
  }

  fun bestUpload(): Upload? {
    if (uploads.isEmpty()) return null

    return when (PowerPreference.getDefaultFile().getString("media_cache_quality")) {
      "quality" -> uploads.maxBy { it.bitrate } ?: uploads[0]
      "size" -> uploads.minBy { it.bitrate } ?: uploads[0]
      else -> uploads.maxBy { it.bitrate } ?: uploads[0]
    }
  }
}

data class Favorite(val id: Int, val track: Track)

data class Playlist(
  val id: Int,
  val name: String,
  val album_covers: List<String>,
  val tracks_count: Int,
  val duration: Int
)

data class PlaylistTrack(val track: Track)