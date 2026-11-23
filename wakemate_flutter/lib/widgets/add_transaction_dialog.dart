import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../providers/app_state.dart';

class AddTransactionDialog extends StatefulWidget {
  final AppState appState;

  const AddTransactionDialog({super.key, required this.appState});

  @override
  State<AddTransactionDialog> createState() => _AddTransactionDialogState();
}

class _AddTransactionDialogState extends State<AddTransactionDialog> {
  final _titleController = TextEditingController();
  final _amountController = TextEditingController();
  String _type = 'expense'; // 'income' or 'expense'
  String _category = 'Food';

  final List<String> _expenseCategories = ['Food', 'Bills', 'Sadqah', 'Shopping', 'Travel', 'General'];
  final List<String> _incomeCategories = ['Salary', 'Business', 'Freelance', 'Investment', 'Gift'];

  @override
  void dispose() {
    _titleController.dispose();
    _amountController.dispose();
    super.dispose();
  }

  void _submit() {
    final title = _titleController.text.trim();
    final amountText = _amountController.text.trim();

    if (title.isEmpty || amountText.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Barah-e-karam naam aur raqam darj karein')),
      );
      return;
    }

    final amount = double.tryParse(amountText);
    if (amount == null || amount <= 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Durust raqam darj karein')),
      );
      return;
    }

    widget.appState.addFinanceItem(
      title: title,
      amount: amount,
      type: _type,
      category: _category,
    );

    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: _type == 'income' ? AppTheme.emeraldIslamic : AppTheme.roseExpense,
        content: Text(_type == 'income' ? 'Amdan shamil ho gayi!' : 'Kharcha darj ho gaya!'),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final categories = _type == 'income' ? _incomeCategories : _expenseCategories;
    if (!categories.contains(_category)) {
      _category = categories.first;
    }

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      backgroundColor: AppTheme.surfaceLight,
      insetPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(22),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Naya Indiraj (Transaction)',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: AppTheme.textPrimary,
                  ),
                ),
                IconButton(
                  onPressed: () => Navigator.pop(context),
                  icon: const Icon(Icons.close_rounded, color: AppTheme.textSecondary),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Type Toggle (Kharcha vs Amdan)
            Container(
              padding: const EdgeInsets.all(4),
              decoration: BoxDecoration(
                color: const Color(0xFFF1F5F9),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  Expanded(
                    child: InkWell(
                      onTap: () => setState(() {
                        _type = 'expense';
                        _category = _expenseCategories.first;
                      }),
                      borderRadius: BorderRadius.circular(10),
                      child: Container(
                        padding: const EdgeInsets.symmetric(vertical: 10),
                        decoration: BoxDecoration(
                          color: _type == 'expense' ? AppTheme.roseExpense : Colors.transparent,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          'Kharcha (Expense)',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: _type == 'expense' ? Colors.white : AppTheme.textSecondary,
                          ),
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: InkWell(
                      onTap: () => setState(() {
                        _type = 'income';
                        _category = _incomeCategories.first;
                      }),
                      borderRadius: BorderRadius.circular(10),
                      child: Container(
                        padding: const EdgeInsets.symmetric(vertical: 10),
                        decoration: BoxDecoration(
                          color: _type == 'income' ? AppTheme.emeraldIslamic : Colors.transparent,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          'Amdan (Income)',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: _type == 'income' ? Colors.white : AppTheme.textSecondary,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Amount field
            TextField(
              controller: _amountController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              decoration: InputDecoration(
                labelText: 'Raqam (PKR Rs)',
                prefixText: 'Rs. ',
                prefixStyle: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
                prefixIcon: Icon(
                  Icons.account_balance_wallet_rounded,
                  color: _type == 'income' ? AppTheme.emeraldIslamic : AppTheme.roseExpense,
                ),
              ),
            ),
            const SizedBox(height: 14),

            // Title field
            TextField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Tafseel (Description / Title)',
                hintText: 'e.g. Biryani, Bijli Bill, Salary, Sadqah',
                prefixIcon: Icon(Icons.edit_note_rounded, color: AppTheme.primary),
              ),
            ),
            const SizedBox(height: 16),

            // Category Chips
            const Text(
              'Category Muntakhib Karein',
              style: TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppTheme.textPrimary),
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: categories.map((cat) {
                final isSelected = _category == cat;
                return ChoiceChip(
                  label: Text(cat),
                  selected: isSelected,
                  selectedColor: _type == 'income' ? AppTheme.emeraldIslamic : AppTheme.roseExpense,
                  labelStyle: TextStyle(
                    color: isSelected ? Colors.white : AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                  backgroundColor: const Color(0xFFF1F5F9),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  onSelected: (selected) {
                    if (selected) setState(() => _category = cat);
                  },
                );
              }).toList(),
            ),
            const SizedBox(height: 22),

            // Save Button
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: _type == 'income' ? AppTheme.emeraldIslamic : AppTheme.roseExpense,
                padding: const EdgeInsets.symmetric(vertical: 14),
              ),
              onPressed: _submit,
              child: const Text(
                'Save Transaction',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
