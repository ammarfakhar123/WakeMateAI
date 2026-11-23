import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/zikr_item.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';

class DigitalTasbeehDialog extends StatefulWidget {
  final ZikrItem item;
  final AppState appState;

  const DigitalTasbeehDialog({
    super.key,
    required this.item,
    required this.appState,
  });

  @override
  State<DigitalTasbeehDialog> createState() => _DigitalTasbeehDialogState();
}

class _DigitalTasbeehDialogState extends State<DigitalTasbeehDialog> {
  late int _count;

  @override
  void initState() {
    super.initState();
    _count = widget.item.currentCount;
  }

  void _increment() {
    HapticFeedback.lightImpact();
    setState(() {
      _count++;
    });
    widget.appState.incrementZikr(widget.item.id);
  }

  void _reset() {
    HapticFeedback.mediumImpact();
    setState(() {
      _count = 0;
    });
    widget.appState.resetZikr(widget.item.id);
  }

  @override
  Widget build(BuildContext context) {
    final target = widget.item.targetCount;
    final progress = target > 0 ? (_count / target).clamp(0.0, 1.0) : 0.0;
    final isCompleted = _count >= target;

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      backgroundColor: AppTheme.surfaceLight,
      insetPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      child: Padding(
        padding: const EdgeInsets.all(22),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Top Bar
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.item.title,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: AppTheme.textPrimary,
                        ),
                      ),
                      Text(
                        widget.item.isCustom ? 'Custom Arabic Azkar' : 'Sunnah Azkar',
                        style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary),
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

            // Arabic Display Card
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 16),
              decoration: BoxDecoration(
                color: const Color(0xFFF8FAFC),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: AppTheme.borderLight),
              ),
              child: Column(
                children: [
                  Text(
                    widget.item.arabic,
                    textDirection: TextDirection.rtl,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      height: 1.8,
                      color: Color(0xFF0F172A),
                    ),
                  ),
                  if (widget.item.urdu.isNotEmpty) ...[
                    const SizedBox(height: 10),
                    Text(
                      widget.item.urdu,
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 13,
                        color: AppTheme.textSecondary,
                        height: 1.4,
                      ),
                    ),
                  ],
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Interactive Circular Counter Tap Zone
            Center(
              child: GestureDetector(
                onTap: _increment,
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    SizedBox(
                      width: 190,
                      height: 190,
                      child: CircularProgressIndicator(
                        value: progress,
                        strokeWidth: 10,
                        backgroundColor: const Color(0xFFE2E8F0),
                        valueColor: AlwaysStoppedAnimation<Color>(
                          isCompleted ? AppTheme.emeraldIslamic : AppTheme.primary,
                        ),
                      ),
                    ),
                    Container(
                      width: 155,
                      height: 155,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: isCompleted ? AppTheme.emeraldBg : const Color(0xFFF1F5F9),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.06),
                            blurRadius: 10,
                            offset: const Offset(0, 4),
                          )
                        ],
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            '$_count',
                            style: TextStyle(
                              fontSize: 44,
                              fontWeight: FontWeight.bold,
                              color: isCompleted ? AppTheme.emeraldIslamic : AppTheme.textPrimary,
                            ),
                          ),
                          Text(
                            'Target: $target',
                            style: const TextStyle(
                              fontSize: 13,
                              color: AppTheme.textSecondary,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          const SizedBox(height: 4),
                          const Text(
                            'TAP TO COUNT',
                            style: TextStyle(
                              fontSize: 10,
                              letterSpacing: 1.2,
                              color: AppTheme.textMuted,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),

            // Completion Banner if reached
            if (isCompleted)
              Container(
                margin: const EdgeInsets.only(bottom: 16),
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                decoration: BoxDecoration(
                  color: AppTheme.emeraldBg,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: AppTheme.emeraldIslamic.withOpacity(0.3)),
                ),
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.check_circle_rounded, color: AppTheme.emeraldIslamic, size: 20),
                    SizedBox(width: 8),
                    Text(
                      'MashaAllah! Target Poora Ho Gaya!',
                      style: TextStyle(
                        color: AppTheme.emeraldIslamic,
                        fontWeight: FontWeight.bold,
                        fontSize: 13,
                      ),
                    ),
                  ],
                ),
              ),

            // Action Buttons
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      side: const BorderSide(color: AppTheme.borderLight),
                    ),
                    onPressed: _reset,
                    icon: const Icon(Icons.refresh_rounded, size: 18, color: AppTheme.textSecondary),
                    label: const Text('Reset', style: TextStyle(color: AppTheme.textSecondary)),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  flex: 2,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: isCompleted ? AppTheme.emeraldIslamic : AppTheme.primary,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                    onPressed: _increment,
                    child: const Text('+1 Shamil Karein', style: TextStyle(fontWeight: FontWeight.bold)),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
