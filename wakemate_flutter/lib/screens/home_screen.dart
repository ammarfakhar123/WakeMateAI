import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';
import '../data/bulk_inspiration_data.dart';
import '../widgets/custom_name_dialog.dart';
import '../widgets/arabic_paste_dialog.dart';
import '../widgets/digital_tasbeeh_dialog.dart';

class HomeScreen extends StatefulWidget {
  final AppState appState;
  final Function(int) onTabChange;

  const HomeScreen({
    super.key,
    required this.appState,
    required this.onTabChange,
  });

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _inspirationType = 0; // 0: Ayat, 1: Hadees, 2: Motivation

  @override
  Widget build(BuildContext context) {
    final state = widget.appState;
    final currencyFormatter = NumberFormat.currency(locale: 'en_PK', symbol: 'Rs. ', decimalDigits: 0);

    // Current inspirations
    final currentAyat = BulkInspirationData.ayats[state.ayatIndex % BulkInspirationData.ayats.length];
    final currentHadees = BulkInspirationData.hadees[state.hadeesIndex % BulkInspirationData.hadees.length];
    final currentMotivation = BulkInspirationData.motivation[state.motivationIndex % BulkInspirationData.motivation.length];

    return Scaffold(
      backgroundColor: AppTheme.bgLight,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(vertical: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Top Greeting Bar
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Text(
                              'Assalam-o-Alaikum, ${state.userName} 👋',
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                                color: AppTheme.textPrimary,
                              ),
                            ),
                            IconButton(
                              onPressed: () => showDialog(
                                context: context,
                                builder: (_) => CustomNameDialog(appState: state),
                              ),
                              icon: const Icon(Icons.edit_outlined, size: 18, color: AppTheme.textSecondary),
                              tooltip: 'Naam Tabdeel Karein',
                            ),
                          ],
                        ),
                        Text(
                          DateFormat('EEEE, d MMMM yyyy').format(DateTime.now()),
                          style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary),
                        ),
                      ],
                    ),
                    CircleAvatar(
                      backgroundColor: AppTheme.primary.withOpacity(0.1),
                      child: Text(
                        state.userName.isNotEmpty ? state.userName[0].toUpperCase() : 'A',
                        style: const TextStyle(fontWeight: FontWeight.bold, color: AppTheme.primary),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),

              // 3-Pillar Daily Inspiration Card
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(18),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      // Header & Type Switcher
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(
                            children: [
                              _buildInspirationTab(0, 'Ayat', Icons.menu_book_rounded, AppTheme.emeraldIslamic),
                              const SizedBox(width: 6),
                              _buildInspirationTab(1, 'Hadees', Icons.auto_stories_rounded, AppTheme.secondary),
                              const SizedBox(width: 6),
                              _buildInspirationTab(2, 'Grind', Icons.bolt_rounded, AppTheme.purpleMotivation),
                            ],
                          ),
                          IconButton(
                            onPressed: () => state.shuffleInspirations(),
                            icon: const Icon(Icons.shuffle_rounded, size: 20, color: AppTheme.textSecondary),
                            tooltip: 'Shuffle Inspiration',
                          ),
                        ],
                      ),
                      const Divider(height: 24, color: AppTheme.borderLight),

                      // Card Body
                      if (_inspirationType == 0) ...[
                        Text(
                          currentAyat.arabicOrQuote,
                          textDirection: TextDirection.rtl,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 22,
                            fontWeight: FontWeight.bold,
                            height: 1.8,
                            color: Color(0xFF0F172A),
                          ),
                        ),
                        const SizedBox(height: 12),
                        Text(
                          currentAyat.translationOrDetail,
                          textAlign: TextAlign.center,
                          style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary, height: 1.4),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          currentAyat.reference,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: AppTheme.emeraldIslamic,
                          ),
                        ),
                      ] else if (_inspirationType == 1) ...[
                        Text(
                          currentHadees.arabicOrQuote,
                          textDirection: TextDirection.rtl,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                            height: 1.8,
                            color: Color(0xFF0F172A),
                          ),
                        ),
                        const SizedBox(height: 12),
                        Text(
                          currentHadees.translationOrDetail,
                          textAlign: TextAlign.center,
                          style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary, height: 1.4),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          currentHadees.reference,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: AppTheme.secondary,
                          ),
                        ),
                      ] else ...[
                        Text(
                          '“${currentMotivation.arabicOrQuote}”',
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            height: 1.5,
                            color: Color(0xFF1E1B4B),
                          ),
                        ),
                        const SizedBox(height: 10),
                        Text(
                          currentMotivation.translationOrDetail,
                          textAlign: TextAlign.center,
                          style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary, height: 1.4),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          '⚡ ${currentMotivation.reference}',
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                            color: AppTheme.purpleMotivation,
                          ),
                        ),
                      ],
                      const SizedBox(height: 14),

                      // Next Button
                      Align(
                        alignment: Alignment.centerRight,
                        child: TextButton.icon(
                          onPressed: () {
                            if (_inspirationType == 0) state.nextAyat();
                            if (_inspirationType == 1) state.nextHadees();
                            if (_inspirationType == 2) state.nextMotivation();
                          },
                          icon: const Icon(Icons.arrow_forward_rounded, size: 16, color: AppTheme.primary),
                          label: const Text(
                            'Agla Dekhein',
                            style: TextStyle(fontWeight: FontWeight.bold, color: AppTheme.primary),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 8),

              // Finance Quick Card
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(18),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Row(
                            children: [
                              Icon(Icons.account_balance_wallet_rounded, color: AppTheme.primary, size: 20),
                              SizedBox(width: 8),
                              Text(
                                'Personal Finance',
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
                              ),
                            ],
                          ),
                          TextButton(
                            onPressed: () => widget.onTabChange(1),
                            child: const Text('Manage >', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text('Net Balance', style: TextStyle(fontSize: 12, color: AppTheme.textSecondary)),
                                const SizedBox(height: 4),
                                Text(
                                  currencyFormatter.format(state.netBalance),
                                  style: TextStyle(
                                    fontSize: 22,
                                    fontWeight: FontWeight.bold,
                                    color: state.netBalance >= 0 ? AppTheme.emeraldIslamic : AppTheme.roseExpense,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Container(height: 40, width: 1, color: AppTheme.borderLight),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    const Icon(Icons.arrow_upward_rounded, size: 14, color: AppTheme.emeraldIslamic),
                                    const SizedBox(width: 4),
                                    Text(
                                      currencyFormatter.format(state.totalIncome),
                                      style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppTheme.emeraldIslamic),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                Row(
                                  children: [
                                    const Icon(Icons.arrow_downward_rounded, size: 14, color: AppTheme.roseExpense),
                                    const SizedBox(width: 4),
                                    Text(
                                      currencyFormatter.format(state.totalExpense),
                                      style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppTheme.roseExpense),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 8),

              // Daily Azkar Quick Glance
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(18),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Row(
                            children: [
                              Icon(Icons.spa_rounded, color: AppTheme.emeraldIslamic, size: 20),
                              SizedBox(width: 8),
                              Text(
                                'Daily Azkar & Tasbeeh',
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
                              ),
                            ],
                          ),
                          TextButton(
                            onPressed: () => widget.onTabChange(2),
                            child: const Text('Tamam Azkar >', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Total Azkar: ${state.azkarList.length} (${state.azkarList.where((e) => e.isCustom).length} Custom Arabic Pasted)',
                        style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary),
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: ElevatedButton.icon(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: AppTheme.emeraldIslamic,
                                padding: const EdgeInsets.symmetric(vertical: 10),
                              ),
                              onPressed: () => showDialog(
                                context: context,
                                builder: (_) => ArabicPasteDialog(appState: state),
                              ),
                              icon: const Icon(Icons.paste_rounded, size: 16),
                              label: const Text('+ Arabic Paste', style: TextStyle(fontSize: 13)),
                            ),
                          ),
                          const SizedBox(width: 10),
                          if (state.azkarList.isNotEmpty)
                            Expanded(
                              child: OutlinedButton.icon(
                                style: OutlinedButton.styleFrom(
                                  padding: const EdgeInsets.symmetric(vertical: 10),
                                  side: const BorderSide(color: AppTheme.emeraldIslamic),
                                ),
                                onPressed: () => showDialog(
                                  context: context,
                                  builder: (_) => DigitalTasbeehDialog(
                                    item: state.azkarList.first,
                                    appState: state,
                                  ),
                                ),
                                icon: const Icon(Icons.touch_app_rounded, size: 16, color: AppTheme.emeraldIslamic),
                                label: const Text('Tasbeeh Shuru', style: TextStyle(fontSize: 13, color: AppTheme.emeraldIslamic)),
                              ),
                            ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInspirationTab(int type, String label, IconData icon, Color color) {
    final isSelected = _inspirationType == type;
    return InkWell(
      onTap: () => setState(() => _inspirationType = type),
      borderRadius: BorderRadius.circular(8),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.12) : Colors.transparent,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(
            color: isSelected ? color : Colors.transparent,
            width: 1,
          ),
        ),
        child: Row(
          children: [
            Icon(icon, size: 14, color: isSelected ? color : AppTheme.textSecondary),
            const SizedBox(width: 4),
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                color: isSelected ? color : AppTheme.textSecondary,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
