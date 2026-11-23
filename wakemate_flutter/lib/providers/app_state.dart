import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/finance_item.dart';
import '../models/zikr_item.dart';
import '../models/alarm_item.dart';
import '../data/default_azkar_data.dart';
import '../data/bulk_inspiration_data.dart';

class AppState extends ChangeNotifier {
  String _userName = 'Ammar';
  int _ayatIndex = 0;
  int _hadeesIndex = 0;
  int _motivationIndex = 0;

  List<FinanceItem> _financeItems = [];
  List<ZikrItem> _azkarList = [];
  List<AlarmItem> _alarms = [];

  bool _isLoaded = false;

  String get userName => _userName;
  int get ayatIndex => _ayatIndex;
  int get hadeesIndex => _hadeesIndex;
  int get motivationIndex => _motivationIndex;

  List<FinanceItem> get financeItems => List.unmodifiable(_financeItems);
  List<ZikrItem> get azkarList => List.unmodifiable(_azkarList);
  List<AlarmItem> get alarms => List.unmodifiable(_alarms);
  bool get isLoaded => _isLoaded;

  // Finance calculations
  double get totalIncome => _financeItems
      .where((item) => item.type == 'income')
      .fold(0.0, (sum, item) => sum + item.amount);

  double get totalExpense => _financeItems
      .where((item) => item.type == 'expense')
      .fold(0.0, (sum, item) => sum + item.amount);

  double get netBalance => totalIncome - totalExpense;

  AppState() {
    _loadFromStorage();
  }

  Future<void> _loadFromStorage() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _userName = prefs.getString('user_name') ?? 'Ammar';
      
      // Load Finance
      final financeRaw = prefs.getString('finance_items');
      if (financeRaw != null && financeRaw.isNotEmpty) {
        final List decoded = jsonDecode(financeRaw);
        _financeItems = decoded.map((e) => FinanceItem.fromJson(e)).toList();
      } else {
        // Sample starter data
        _financeItems = [
          FinanceItem(
            id: 'f1',
            title: 'Monthly Salary',
            amount: 85000,
            type: 'income',
            category: 'Salary',
            date: DateTime.now().subtract(const Duration(days: 2)),
          ),
          FinanceItem(
            id: 'f2',
            title: 'Sadqah / Khairat',
            amount: 5000,
            type: 'expense',
            category: 'Sadqah',
            date: DateTime.now().subtract(const Duration(days: 1)),
          ),
          FinanceItem(
            id: 'f3',
            title: 'Groceries & Food',
            amount: 6200,
            type: 'expense',
            category: 'Food',
            date: DateTime.now(),
          ),
        ];
      }

      // Load Azkar
      final azkarRaw = prefs.getString('azkar_items');
      if (azkarRaw != null && azkarRaw.isNotEmpty) {
        final List decoded = jsonDecode(azkarRaw);
        _azkarList = decoded.map((e) => ZikrItem.fromJson(e)).toList();
      } else {
        _azkarList = List.from(DefaultAzkarData.defaultList);
      }

      // Load Alarms
      final alarmsRaw = prefs.getString('alarms');
      if (alarmsRaw != null && alarmsRaw.isNotEmpty) {
        final List decoded = jsonDecode(alarmsRaw);
        _alarms = decoded.map((e) => AlarmItem.fromJson(e)).toList();
      } else {
        _alarms = [
          AlarmItem(id: 'al1', hour: 5, minute: 0, label: 'Fajr & Morning Grind', isEnabled: true),
          AlarmItem(id: 'al2', hour: 7, minute: 30, label: 'Work & Hustle', isEnabled: false),
        ];
      }
    } catch (e) {
      debugPrint("Error loading storage: $e");
    } finally {
      _isLoaded = true;
      notifyListeners();
    }
  }

  Future<void> _saveFinance() async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = jsonEncode(_financeItems.map((e) => e.toJson()).toList());
    await prefs.setString('finance_items', encoded);
  }

  Future<void> _saveAzkar() async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = jsonEncode(_azkarList.map((e) => e.toJson()).toList());
    await prefs.setString('azkar_items', encoded);
  }

  Future<void> _saveAlarms() async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = jsonEncode(_alarms.map((e) => e.toJson()).toList());
    await prefs.setString('alarms', encoded);
  }

  // User Name actions
  Future<void> setUserName(String name) async {
    if (name.trim().isEmpty) return;
    _userName = name.trim();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('user_name', _userName);
    notifyListeners();
  }

  // Inspiration navigation
  void nextAyat() {
    _ayatIndex = (_ayatIndex + 1) % BulkInspirationData.ayats.length;
    notifyListeners();
  }

  void nextHadees() {
    _hadeesIndex = (_hadeesIndex + 1) % BulkInspirationData.hadees.length;
    notifyListeners();
  }

  void nextMotivation() {
    _motivationIndex = (_motivationIndex + 1) % BulkInspirationData.motivation.length;
    notifyListeners();
  }

  void shuffleInspirations() {
    _ayatIndex = (_ayatIndex + 2) % BulkInspirationData.ayats.length;
    _hadeesIndex = (_hadeesIndex + 2) % BulkInspirationData.hadees.length;
    _motivationIndex = (_motivationIndex + 2) % BulkInspirationData.motivation.length;
    notifyListeners();
  }

  // Finance actions
  void addFinanceItem({
    required String title,
    required double amount,
    required String type,
    required String category,
  }) {
    final item = FinanceItem(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      title: title,
      amount: amount,
      type: type,
      category: category,
      date: DateTime.now(),
    );
    _financeItems.insert(0, item);
    _saveFinance();
    notifyListeners();
  }

  void deleteFinanceItem(String id) {
    _financeItems.removeWhere((item) => item.id == id);
    _saveFinance();
    notifyListeners();
  }

  // Azkar actions
  void addCustomArabicZikr({
    required String title,
    required String arabic,
    required String urdu,
    required int targetCount,
  }) {
    final item = ZikrItem(
      id: 'custom_${DateTime.now().millisecondsSinceEpoch}',
      title: title,
      arabic: arabic.trim(),
      urdu: urdu.trim(),
      targetCount: targetCount,
      currentCount: 0,
      isCustom: true,
    );
    _azkarList.insert(0, item);
    _saveAzkar();
    notifyListeners();
  }

  void incrementZikr(String id) {
    final index = _azkarList.indexWhere((e) => e.id == id);
    if (index != -1) {
      _azkarList[index].currentCount++;
      _saveAzkar();
      notifyListeners();
    }
  }

  void resetZikr(String id) {
    final index = _azkarList.indexWhere((e) => e.id == id);
    if (index != -1) {
      _azkarList[index].currentCount = 0;
      _saveAzkar();
      notifyListeners();
    }
  }

  void deleteCustomZikr(String id) {
    _azkarList.removeWhere((e) => e.id == id && e.isCustom);
    _saveAzkar();
    notifyListeners();
  }

  // Alarm actions
  void toggleAlarm(String id) {
    final index = _alarms.indexWhere((e) => e.id == id);
    if (index != -1) {
      _alarms[index].isEnabled = !_alarms[index].isEnabled;
      _saveAlarms();
      notifyListeners();
    }
  }

  void addAlarm(int hour, int minute, String label) {
    _alarms.add(AlarmItem(
      id: 'al_${DateTime.now().millisecondsSinceEpoch}',
      hour: hour,
      minute: minute,
      label: label,
      isEnabled: true,
    ));
    _saveAlarms();
    notifyListeners();
  }

  void deleteAlarm(String id) {
    _alarms.removeWhere((e) => e.id == id);
    _saveAlarms();
    notifyListeners();
  }
}
