package com.example.playlistmaker.data.dto

data class TrackDTO (
        val trackId: String, //id
        val trackName: String, // Название композиции
        val collectionName: String,//название альбома
        val releaseDate: String, //год
        val primaryGenreName: String,// Жанр
        val country: String, // страна
        val artistName: String, // Имя исполнителя
        val trackTimeMillis: Int,// Продолжительность трека
        val artworkUrl100: String, // Ссылка на изображение обложки
        val previewUrl: String // ссылка на трек
)