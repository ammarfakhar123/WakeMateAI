import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';
import '../widgets/custom_name_dialog.dart';

class SettingsScreen extends StatelessWidget {
  final AppState appState;

  const SettingsScreen({super.key, required this.appState});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgLight,
      appBar: AppBar(
        title: const Text('Settings & Profile'),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(vertical: 16),
        children: [
          // Profile Card
          Card(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: AppTheme.primary.withOpacity(0.1),
                child: const Icon(Icons.person_rounded, color: AppTheme.primary),
              ),
              title: Text(
                appState.userName,
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
              ),
              subtitle: const Text('Aapka Display Name'),
              trailing: TextButton(
                onPressed: () => showDialog(
                  context: context,
                  builder: (_) => CustomNameDialog(appState: appState),
                ),
                child: const Text('Edit'),
              ),
            ),
          ),
          const SizedBox(height: 12),

          // Features Summary
          Card(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Column(
              children: [
                const ListTile(
                  leading: Icon(Icons.palette_outlined, color: AppTheme.primary),
                  title: Text('Design Theme'),
                  subtitle: Text('Modern Light Theme (Slate & Emerald)'),
                  trailing: Text('Light', style: TextStyle(fontWeight: FontWeight.bold, color: AppTheme.emeraldIslamic)),
                ),
                const Divider(height: 1, indent: 60, color: AppTheme.borderLight),
                ListTile(
                  leading: const Icon(Icons.account_balance_wallet_outlined, color: AppTheme.secondary),
                  title: const Text('Currency'),
                  subtitle: const Text('Pakistani Rupee (PKR Rs.)'),
                  trailing: Text('PKR', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey.shade700)),
                ),
                const Divider(height: 1, indent: 60, color: AppTheme.borderLight),
                ListTile(
                  leading: const Icon(Icons.auto_stories_outlined, color: AppTheme.emeraldIslamic),
                  title: const Text('Custom Arabic Azkar'),
                  subtitle: Text('${appState.azkarList.where((e) => e.isCustom).length} Custom Saved'),
                  trailing: const Icon(Icons.check_circle_rounded, color: AppTheme.emeraldIslamic, size: 20),
                ),
              ],
            ),
          ),
          const SizedBox(height: 12),

          // App Info
          const Card(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Padding(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'WakeMate AI - Flutter Edition',
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: AppTheme.textPrimary),
                  ),
                  SizedBox(height: 4),
                  Text(
                    'Version 1.1.0 • Built with Flutter & Material 3\nDesigned for Morning Discipline, Financial Clarity & Spiritual Upliftment.',
                    style: TextStyle(fontSize: 12, color: AppTheme.textSecondary, height: 1.4),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
