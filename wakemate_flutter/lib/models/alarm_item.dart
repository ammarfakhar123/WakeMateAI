class AlarmItem {
  final String id;
  int hour;
  int minute;
  String label;
  bool isEnabled;
  bool wakeUpCheck;
  bool chargeChallenge;

  AlarmItem({
    required this.id,
    required this.hour,
    required this.minute,
    required this.label,
    this.isEnabled = true,
    this.wakeUpCheck = true,
    this.chargeChallenge = true,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'hour': hour,
    'minute': minute,
    'label': label,
    'isEnabled': isEnabled,
    'wakeUpCheck': wakeUpCheck,
    'chargeChallenge': chargeChallenge,
  };

  factory AlarmItem.fromJson(Map<String, dynamic> json) => AlarmItem(
    id: json['id'] as String,
    hour: json['hour'] as int,
    minute: json['minute'] as int,
    label: json['label'] as String,
    isEnabled: json['isEnabled'] as bool? ?? true,
    wakeUpCheck: json['wakeUpCheck'] as bool? ?? true,
    chargeChallenge: json['chargeChallenge'] as bool? ?? true,
  );
}

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
