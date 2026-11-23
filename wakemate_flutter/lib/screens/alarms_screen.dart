import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';

class AlarmsScreen extends StatelessWidget {
  final AppState appState;

  const AlarmsScreen({super.key, required this.appState});

  @override
  Widget build(BuildContext context) {
    final alarms = appState.alarms;

    return Scaffold(
      backgroundColor: AppTheme.bgLight,
      appBar: AppBar(
        title: const Text('Smart Morning Alarms'),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Add default alarm
          appState.addAlarm(6, 0, 'Morning Awakening');
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Naya 6:00 AM alarm shamil ho gaya!')),
          );
        },
        backgroundColor: AppTheme.primary,
        child: const Icon(Icons.add_alarm_rounded, color: Colors.white),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(vertical: 12),
        children: [
          // Challenge Features banner
          Card(
            color: AppTheme.purpleBg,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
              side: BorderSide(color: AppTheme.purpleMotivation.withOpacity(0.3)),
            ),
            child: const Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.bolt_rounded, color: AppTheme.purpleMotivation, size: 20),
                      SizedBox(width: 8),
                      Text(
                        'Anti-Snooze & Wake Verification',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Color(0xFF1E1B4B)),
                      ),
                    ],
                  ),
                  SizedBox(height: 6),
                  Text(
                    'WakeMate AI alarm band hone ke 5 minute baad check karega ke aap waqai jaag chukay hain ya wapas so gaye. Agar charger disconnect kiya to buzzer bajega!',
                    style: TextStyle(fontSize: 12, color: AppTheme.textSecondary, height: 1.3),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 8),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Text(
              'Aapke Alarms (${alarms.length})',
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
            ),
          ),

          ...alarms.map((alarm) {
            final timeStr = '${alarm.hour.toString().padLeft(2, '0')}:${alarm.minute.toString().padLeft(2, '0')} ${alarm.hour >= 12 ? 'PM' : 'AM'}';
            return Card(
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            timeStr,
                            style: TextStyle(
                              fontSize: 28,
                              fontWeight: FontWeight.bold,
                              color: alarm.isEnabled ? AppTheme.textPrimary : AppTheme.textMuted,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            alarm.label,
                            style: TextStyle(
                              fontSize: 14,
                              fontWeight: FontWeight.w500,
                              color: alarm.isEnabled ? AppTheme.primary : AppTheme.textMuted,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Row(
                            children: [
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF1F5F9),
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: const Text('5-Min Check: Active', style: TextStyle(fontSize: 10, color: AppTheme.textSecondary)),
                              ),
                              const SizedBox(width: 6),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF1F5F9),
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: const Text('Charging Challenge: On', style: TextStyle(fontSize: 10, color: AppTheme.textSecondary)),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                    Switch(
                      value: alarm.isEnabled,
                      activeColor: AppTheme.primary,
                      onChanged: (val) => appState.toggleAlarm(alarm.id),
                    ),
                    IconButton(
                      onPressed: () => appState.deleteAlarm(alarm.id),
                      icon: const Icon(Icons.delete_outline_rounded, size: 20, color: AppTheme.textMuted),
                    ),
                  ],
                ),
              ),
            );
          }),
        ],
      ),
    );
  }
}
