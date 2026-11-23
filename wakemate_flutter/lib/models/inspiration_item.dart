class InspirationItem {
  final String id;
  final String type; // 'ayat', 'hadees', 'motivation'
  final String title;
  final String arabicOrQuote;
  final String translationOrDetail;
  final String reference;

  InspirationItem({
    required this.id,
    required this.type,
    required this.title,
    required this.arabicOrQuote,
    required this.translationOrDetail,
    required this.reference,
  });
}
