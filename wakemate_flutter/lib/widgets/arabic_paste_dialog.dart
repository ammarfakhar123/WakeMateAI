import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';

class ArabicPasteDialog extends StatefulWidget {
  final AppState appState;

  const ArabicPasteDialog({super.key, required this.appState});

  @override
  State<ArabicPasteDialog> createState() => _ArabicPasteDialogState();
}

class _ArabicPasteDialogState extends State<ArabicPasteDialog> {
  final _titleController = TextEditingController(text: 'Mera Naya Wazifa');
  final _arabicController = TextEditingController();
  final _urduController = TextEditingController();
  int _targetCount = 33;

  @override
  void dispose() {
    _titleController.dispose();
    _arabicController.dispose();
    _urduController.dispose();
    super.dispose();
  }

  Future<void> _pasteFromClipboard() async {
    final data = await Clipboard.getData(Clipboard.kTextPlain);
    if (data?.text != null && data!.text!.isNotEmpty) {
      setState(() {
        _arabicController.text = data.text!;
      });
    }
  }

  void _submit() {
    final arabic = _arabicController.text.trim();
    if (arabic.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Barah-e-karam Arabic matan (text) paste karein')),
      );
      return;
    }

    widget.appState.addCustomArabicZikr(
      title: _titleController.text.trim().isEmpty ? 'Khas Zikr' : _titleController.text.trim(),
      arabic: arabic,
      urdu: _urduController.text.trim(),
      targetCount: _targetCount,
    );

    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.emeraldIslamic,
        content: Text('MashaAllah! Naya Arabic Zikr shamil ho gaya hai!'),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      backgroundColor: AppTheme.surfaceLight,
      insetPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(
                    color: AppTheme.emeraldBg,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: const Icon(Icons.menu_book_rounded, color: AppTheme.emeraldIslamic),
                ),
                const SizedBox(width: 12),
                const Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Custom Arabic Azkar Paste',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: AppTheme.textPrimary,
                        ),
                      ),
                      Text(
                        'Apna matlooba Arabic zikr yahan paste karein',
                        style: TextStyle(fontSize: 12, color: AppTheme.textSecondary),
                      ),
                    ],
                  ),
                ),
                IconButton(
                  onPressed: () => Navigator.pop(context),
                  icon: const Icon(Icons.close_rounded, color: AppTheme.textSecondary),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // Title input
            TextField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Zikr / Dua ka Naam',
                hintText: 'e.g. Dua-e-Khas, Darood, Wazifa',
                prefixIcon: Icon(Icons.label_outline, color: AppTheme.primary),
              ),
            ),
            const SizedBox(height: 14),

            // Arabic text input with paste button
            Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Arabic Matan (عربی متن)',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: AppTheme.textPrimary,
                      ),
                    ),
                    TextButton.icon(
                      onPressed: _pasteFromClipboard,
                      icon: const Icon(Icons.paste_rounded, size: 16, color: AppTheme.emeraldIslamic),
                      label: const Text(
                        'Paste from Clipboard',
                        style: TextStyle(fontSize: 12, color: AppTheme.emeraldIslamic, fontWeight: FontWeight.bold),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                TextField(
                  controller: _arabicController,
                  maxLines: 4,
                  textDirection: TextDirection.rtl,
                  style: const TextStyle(
                    fontSize: 20,
                    height: 1.8,
                    fontWeight: FontWeight.w500,
                    color: Color(0xFF0F172A),
                  ),
                  decoration: const InputDecoration(
                    hintText: 'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ...',
                    hintTextDirection: TextDirection.rtl,
                    fillColor: Color(0xFFF8FAFC),
                  ),
                  onChanged: (val) => setState(() {}),
                ),
              ],
            ),
            const SizedBox(height: 14),

            // Urdu translation (optional)
            TextField(
              controller: _urduController,
              decoration: const InputDecoration(
                labelText: 'Urdu Tarjuma / Maani (Ikhtiyari)',
                hintText: 'Matlab ya faida likhein',
                prefixIcon: Icon(Icons.translate_rounded, color: AppTheme.secondary),
              ),
            ),
            const SizedBox(height: 16),

            // Target Count Selector
            const Text(
              'Target Tasbeeh Count',
              style: TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppTheme.textPrimary),
            ),
            const SizedBox(height: 8),
            Row(
              children: [33, 100, 313, 1000].map((count) {
                final isSelected = _targetCount == count;
                return Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 3),
                    child: InkWell(
                      onTap: () => setState(() => _targetCount = count),
                      borderRadius: BorderRadius.circular(10),
                      child: Container(
                        padding: const EdgeInsets.symmetric(vertical: 10),
                        decoration: BoxDecoration(
                          color: isSelected ? AppTheme.emeraldIslamic : const Color(0xFFF1F5F9),
                          borderRadius: BorderRadius.circular(10),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          '$count',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: isSelected ? Colors.white : AppTheme.textPrimary,
                          ),
                        ),
                      ),
                    ),
                  ),
                );
              }).toList(),
            ),
            const SizedBox(height: 20),

            // Submit Button
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.emeraldIslamic,
                padding: const EdgeInsets.symmetric(vertical: 14),
              ),
              onPressed: _submit,
              child: const Text(
                'Shamil Karein & Tasbeeh Shuru Karein',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
