import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';
import '../models/zikr_item.dart';
import '../widgets/arabic_paste_dialog.dart';
import '../widgets/digital_tasbeeh_dialog.dart';

class AzkarScreen extends StatefulWidget {
  final AppState appState;

  const AzkarScreen({super.key, required this.appState});

  @override
  State<AzkarScreen> createState() => _AzkarScreenState();
}

class _AzkarScreenState extends State<AzkarScreen> {
  int _filterIndex = 0; // 0: All, 1: Custom Pasted, 2: Sunnah

  @override
  Widget build(BuildContext context) {
    final state = widget.appState;
    List<ZikrItem> list = state.azkarList;
    if (_filterIndex == 1) {
      list = list.where((e) => e.isCustom).toList();
    } else if (_filterIndex == 2) {
      list = list.where((e) => !e.isCustom).toList();
    }

    return Scaffold(
      backgroundColor: AppTheme.bgLight,
      appBar: AppBar(
        title: const Text('Daily Azkar & Tasbeeh'),
        actions: [
          IconButton(
            onPressed: () => showDialog(
              context: context,
              builder: (_) => ArabicPasteDialog(appState: state),
            ),
            icon: const Icon(Icons.paste_rounded, color: AppTheme.emeraldIslamic),
            tooltip: 'Paste Custom Arabic',
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => showDialog(
          context: context,
          builder: (_) => ArabicPasteDialog(appState: state),
        ),
        backgroundColor: AppTheme.emeraldIslamic,
        icon: const Icon(Icons.add_rounded, color: Colors.white),
        label: const Text('Paste Arabic Azkar', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Top Action Promo Card
            Card(
              color: AppTheme.emeraldBg,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(color: AppTheme.emeraldIslamic.withOpacity(0.3)),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Icon(Icons.auto_stories_rounded, color: AppTheme.emeraldIslamic, size: 28),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            'Arabic Wazifa / Dua Paste Karein',
                            style: TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.bold,
                              color: AppTheme.textPrimary,
                            ),
                          ),
                          const SizedBox(height: 4),
                          const Text(
                            'Aap kisi bhi Arabic text ko yahan paste kar ke apni marzi ki tasbeeh chala saktay hain.',
                            style: TextStyle(fontSize: 12, color: AppTheme.textSecondary, height: 1.3),
                          ),
                          const SizedBox(height: 10),
                          ElevatedButton.icon(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: AppTheme.emeraldIslamic,
                              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                              visualDensity: VisualDensity.compact,
                            ),
                            onPressed: () => showDialog(
                              context: context,
                              builder: (_) => ArabicPasteDialog(appState: state),
                            ),
                            icon: const Icon(Icons.paste_rounded, size: 14),
                            label: const Text('Paste Arabic Text', style: TextStyle(fontSize: 12)),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 12),

            // Filter Tabs
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  _buildFilterTab(0, 'Tamam (${state.azkarList.length})'),
                  const SizedBox(width: 8),
                  _buildFilterTab(1, 'Custom Pasted (${state.azkarList.where((e) => e.isCustom).length})'),
                  const SizedBox(width: 8),
                  _buildFilterTab(2, 'Sunnah (${state.azkarList.where((e) => !e.isCustom).length})'),
                ],
              ),
            ),
            const SizedBox(height: 12),

            // Azkar List
            if (list.isEmpty)
              Padding(
                padding: const EdgeInsets.all(32),
                child: Column(
                  children: [
                    const Icon(Icons.menu_book_rounded, size: 48, color: AppTheme.textMuted),
                    const SizedBox(height: 12),
                    Text(
                      _filterIndex == 1
                          ? 'Abhi tak koi Arabic text paste nahi kiya gaya'
                          : 'Koi Azkar dastiyab nahi hai',
                      style: const TextStyle(color: AppTheme.textSecondary),
                    ),
                  ],
                ),
              )
            else
              ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: list.length,
                itemBuilder: (context, index) {
                  final item = list[index];
                  final progress = item.targetCount > 0 ? (item.currentCount / item.targetCount).clamp(0.0, 1.0) : 0.0;
                  final isDone = item.currentCount >= item.targetCount;

                  return Card(
                    margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          // Top info row
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                    decoration: BoxDecoration(
                                      color: item.isCustom ? AppTheme.purpleBg : AppTheme.emeraldBg,
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Text(
                                      item.isCustom ? 'Custom Pasted' : 'Sunnah',
                                      style: TextStyle(
                                        fontSize: 10,
                                        fontWeight: FontWeight.bold,
                                        color: item.isCustom ? AppTheme.purpleMotivation : AppTheme.emeraldIslamic,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(width: 8),
                                  Text(
                                    item.title,
                                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: AppTheme.textPrimary),
                                  ),
                                ],
                              ),
                              if (item.isCustom)
                                IconButton(
                                  onPressed: () => state.deleteCustomZikr(item.id),
                                  icon: const Icon(Icons.delete_outline_rounded, size: 18, color: AppTheme.textMuted),
                                  tooltip: 'Delete',
                                ),
                            ],
                          ),
                          const SizedBox(height: 10),

                          // Arabic Text (RTL)
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                            decoration: BoxDecoration(
                              color: const Color(0xFFF8FAFC),
                              borderRadius: BorderRadius.circular(12),
                              border: Border.all(color: AppTheme.borderLight),
                            ),
                            child: Text(
                              item.arabic,
                              textDirection: TextDirection.rtl,
                              textAlign: TextAlign.center,
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                                height: 1.8,
                                color: Color(0xFF0F172A),
                              ),
                            ),
                          ),
                          if (item.urdu.isNotEmpty) ...[
                            const SizedBox(height: 8),
                            Text(
                              item.urdu,
                              style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary, height: 1.3),
                            ),
                          ],
                          const SizedBox(height: 14),

                          // Progress bar & Counter
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: [
                                  Icon(
                                    isDone ? Icons.check_circle_rounded : Icons.radio_button_unchecked_rounded,
                                    size: 16,
                                    color: isDone ? AppTheme.emeraldIslamic : AppTheme.textSecondary,
                                  ),
                                  const SizedBox(width: 6),
                                  Text(
                                    '${item.currentCount} / ${item.targetCount}',
                                    style: TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 14,
                                      color: isDone ? AppTheme.emeraldIslamic : AppTheme.textPrimary,
                                    ),
                                  ),
                                ],
                              ),
                              Row(
                                children: [
                                  TextButton(
                                    onPressed: () => state.resetZikr(item.id),
                                    child: const Text('Reset', style: TextStyle(fontSize: 12, color: AppTheme.textSecondary)),
                                  ),
                                  ElevatedButton.icon(
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: isDone ? AppTheme.emeraldIslamic : AppTheme.primary,
                                      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                    ),
                                    onPressed: () => showDialog(
                                      context: context,
                                      builder: (_) => DigitalTasbeehDialog(
                                        item: item,
                                        appState: state,
                                      ),
                                    ),
                                    icon: const Icon(Icons.touch_app_rounded, size: 14),
                                    label: Text(
                                      isDone ? 'MashaAllah' : 'Tasbeeh Shuru',
                                      style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                          const SizedBox(height: 6),
                          ClipRRect(
                            borderRadius: BorderRadius.circular(4),
                            child: LinearProgressIndicator(
                              value: progress,
                              minHeight: 6,
                              backgroundColor: const Color(0xFFE2E8F0),
                              valueColor: AlwaysStoppedAnimation<Color>(
                                isDone ? AppTheme.emeraldIslamic : AppTheme.primary,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            const SizedBox(height: 80),
          ],
        ),
      ),
    );
  }

  Widget _buildFilterTab(int index, String label) {
    final isSelected = _filterIndex == index;
    return InkWell(
      onTap: () => setState(() => _filterIndex = index),
      borderRadius: BorderRadius.circular(8),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? AppTheme.primary : const Color(0xFFF1F5F9),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.bold,
            color: isSelected ? Colors.white : AppTheme.textSecondary,
          ),
        ),
      ),
    );
  }
}
