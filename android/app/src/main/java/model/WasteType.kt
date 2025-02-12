package model

enum class WasteType {
    CARDBOARD,
    GLASS,
    METAL,
    PAPER,
    PLASTIC,
    UNKNOWN;

    // capitalize
    override fun toString(): String {
        val range = 1 until name.length
        return name
            .replaceRange(
                range = range,
                replacement = name.substring(range).lowercase()
            )
    }
}