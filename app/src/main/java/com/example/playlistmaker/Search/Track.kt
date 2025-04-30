package com.example.playlistmaker.Search

data class Track (
    val trackId: String, //id
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Int,// Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
)