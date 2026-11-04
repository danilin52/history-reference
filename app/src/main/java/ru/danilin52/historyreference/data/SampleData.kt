package ru.danilin52.historyreference.data

object SampleData {

    val events = listOf(
        HistoricalEvent(
            date = "1240-07-15",
            title = "Невская битва",
            description = "Александр Невский разбил шведский отряд на реке Неве, за что получил своё прозвище.",
            category = EventCategory.WAR,
            place = "Река Нева",
            importance = 8,
            imageUrl = null
        ),
        HistoricalEvent(
            date = "1380-09-08",
            title = "Куликовская битва",
            description = "Дмитрий Донской одержал победу над войском Мамая, ставшую символом объединения русских земель.",
            category = EventCategory.WAR,
            place = "Куликово поле",
            importance = 10,
            imageUrl = null
        ),
        HistoricalEvent(
            date = "1869-03-06",
            title = "Открытие периодического закона",
            description = "Дмитрий Менделеев представил периодическую систему химических элементов.",
            category = EventCategory.SCIENCE,
            place = "Санкт-Петербург",
            importance = 9,
            imageUrl = null
        ),
        HistoricalEvent(
            date = "1961-04-12",
            title = "Первый полёт человека в космос",
            description = "Юрий Гагарин совершил первый в истории полёт в космос на корабле «Восток-1».",
            category = EventCategory.SCIENCE,
            place = "Космодром Байконур",
            importance = 10,
            imageUrl = null
        ),
        HistoricalEvent(
            date = "1812-09-07",
            title = "Бородинское сражение",
            description = "Крупнейшее сражение Отечественной войны 1812 года между русской и французской армиями.",
            category = EventCategory.WAR,
            place = "Бородино",
            importance = 9,
            imageUrl = null
        )
    )

    val persons = listOf(
        Person(
            name = "Александр Невский",
            birthYear = 1221,
            deathYear = 1263,
            biography = "Князь новгородский и владимирский, полководец и дипломат. Победитель Невской битвы и Ледового побоища.",
            portraitUrl = null,
            occupation = "Полководец, князь"
        ),
        Person(
            name = "Дмитрий Донской",
            birthYear = 1350,
            deathYear = 1389,
            biography = "Великий князь московский и владимирский, руководитель победы на Куликовом поле.",
            portraitUrl = null,
            occupation = "Князь"
        ),
        Person(
            name = "Дмитрий Менделеев",
            birthYear = 1834,
            deathYear = 1907,
            biography = "Русский учёный-энциклопедист, химик, автор периодического закона химических элементов.",
            portraitUrl = null,
            occupation = "Учёный-химик"
        ),
        Person(
            name = "Юрий Гагарин",
            birthYear = 1934,
            deathYear = 1968,
            biography = "Первый космонавт Земли, Герой Советского Союза.",
            portraitUrl = null,
            occupation = "Космонавт"
        ),
        Person(
            name = "Михаил Кутузов",
            birthYear = 1745,
            deathYear = 1813,
            biography = "Русский полководец, генерал-фельдмаршал, главнокомандующий в Отечественной войне 1812 года.",
            portraitUrl = null,
            occupation = "Полководец"
        )
    )

    data class Link(val eventTitle: String, val personName: String, val role: EventPersonRole)

    val links: List<Triple<String, String, EventPersonRole>> = listOf(
        Triple("Невская битва", "Александр Невский", EventPersonRole.INITIATOR),
        Triple("Куликовская битва", "Дмитрий Донской", EventPersonRole.INITIATOR),
        Triple("Открытие периодического закона", "Дмитрий Менделеев", EventPersonRole.INITIATOR),
        Triple("Первый полёт человека в космос", "Юрий Гагарин", EventPersonRole.PARTICIPANT),
        Triple("Бородинское сражение", "Михаил Кутузов", EventPersonRole.INITIATOR)
    )
}