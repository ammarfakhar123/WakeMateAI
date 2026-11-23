class FinanceItem {
  final String id;
  final String title;
  final double amount;
  final String type; // 'income' or 'expense'
  final String category; // 'Food', 'Salary', 'Bills', 'Business', 'Sadqah', 'Shopping', 'General'
  final DateTime date;

  FinanceItem({
    required this.id,
    required this.title,
    required this.amount,
    required this.type,
    required this.category,
    required this.date,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'amount': amount,
    'type': type,
    'category': category,
    'date': date.toIso8601String(),
  };

  factory FinanceItem.fromJson(Map<String, dynamic> json) => FinanceItem(
    id: json['id'] as String,
    title: json['title'] as String,
    amount: (json['amount'] as num).toDouble(),
    type: json['type'] as String,
    category: json['category'] as String,
    date: DateTime.parse(json['date'] as String),
  );
}
