class ZikrItem {
  final String id;
  final String title;
  final String arabic;
  final String urdu;
  final int targetCount;
  int currentCount;
  final bool isCustom;

  ZikrItem({
    required this.id,
    required this.title,
    required this.arabic,
    required this.urdu,
    required this.targetCount,
    this.currentCount = 0,
    this.isCustom = false,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'arabic': arabic,
    'urdu': urdu,
    'targetCount': targetCount,
    'currentCount': currentCount,
    'isCustom': isCustom,
  };

  factory ZikrItem.fromJson(Map<String, dynamic> json) => ZikrItem(
    id: json['id'] as String,
    title: json['title'] as String,
    arabic: json['arabic'] as String,
    urdu: json['urdu'] as String? ?? '',
    targetCount: (json['targetCount'] as num).toInt(),
    currentCount: (json['currentCount'] as num?)?.toInt() ?? 0,
    isCustom: json['isCustom'] as bool? ?? false,
  );
}
