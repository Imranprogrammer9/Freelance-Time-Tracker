@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package dev.shipkaro.kit.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.components.KitAvatar
import dev.shipkaro.kit.core.designsystem.components.KitBadge
import dev.shipkaro.kit.core.designsystem.components.KitBanner
import dev.shipkaro.kit.core.designsystem.components.KitBannerStyle
import dev.shipkaro.kit.core.designsystem.components.KitBottomNavBar
import dev.shipkaro.kit.core.designsystem.components.KitBottomNavItem
import dev.shipkaro.kit.core.designsystem.components.KitBottomSheet
import dev.shipkaro.kit.core.designsystem.components.KitBulletRow
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.components.KitCard
import dev.shipkaro.kit.core.designsystem.components.KitChip
import dev.shipkaro.kit.core.designsystem.components.KitDatePicker
import dev.shipkaro.kit.core.designsystem.components.KitDialog
import dev.shipkaro.kit.core.designsystem.components.KitDisclosureGroup
import dev.shipkaro.kit.core.designsystem.components.KitDropdownMenu
import dev.shipkaro.kit.core.designsystem.components.KitFab
import dev.shipkaro.kit.core.designsystem.components.KitFeatureRow
import dev.shipkaro.kit.core.designsystem.components.KitFilterChip
import dev.shipkaro.kit.core.designsystem.components.KitIndeterminateLinearProgress
import dev.shipkaro.kit.core.designsystem.components.KitLinearProgress
import dev.shipkaro.kit.core.designsystem.components.KitListItem
import dev.shipkaro.kit.core.designsystem.components.KitOtpField
import dev.shipkaro.kit.core.designsystem.components.KitPasswordField
import dev.shipkaro.kit.core.designsystem.components.KitPricingCard
import dev.shipkaro.kit.core.designsystem.components.KitRatingBar
import dev.shipkaro.kit.core.designsystem.components.KitSearchField
import dev.shipkaro.kit.core.designsystem.components.KitSegmentedButton
import dev.shipkaro.kit.core.designsystem.components.KitSkeletonListItem
import dev.shipkaro.kit.core.designsystem.components.KitSlider
import dev.shipkaro.kit.core.designsystem.components.KitSnackbarHost
import dev.shipkaro.kit.core.designsystem.components.KitStepper
import dev.shipkaro.kit.core.designsystem.components.KitSwipeableRow
import dev.shipkaro.kit.core.designsystem.components.KitTextField
import dev.shipkaro.kit.core.designsystem.components.rememberKitSnackbarController
import dev.shipkaro.kit.core.util.EmailLauncher
import dev.shipkaro.kit.core.util.rememberKitHaptics
import dev.shipkaro.kit.core.designsystem.ops.KitUpdateSheet
import dev.shipkaro.kit.core.designsystem.ops.KitWhatsNewSheet
import dev.shipkaro.kit.core.designsystem.settings.AccountRow
import dev.shipkaro.kit.core.designsystem.settings.DangerRow
import dev.shipkaro.kit.core.designsystem.settings.LegalLinks
import dev.shipkaro.kit.core.designsystem.settings.NavRow
import dev.shipkaro.kit.core.designsystem.settings.SettingsDivider
import dev.shipkaro.kit.core.designsystem.settings.SettingsSection
import dev.shipkaro.kit.core.designsystem.settings.ToggleRow
import dev.shipkaro.kit.core.designsystem.state.KitEmptyState
import dev.shipkaro.kit.core.designsystem.state.KitErrorState
import dev.shipkaro.kit.core.designsystem.state.KitInlineLoading
import dev.shipkaro.kit.core.designsystem.state.KitSuccessState
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Live catalogue of every kit component, grouped by section. Used as a sanity
 * check after `/kit-setup-theme` to confirm the chosen brand color + icon pack
 * looks right across the whole design system. Reachable from Home → "Browse
 * components".
 *
 * Intentionally non-interactive (or dummy-interactive): every button onClick is
 * a no-op, every text field is local state. Nothing here mutates app state.
 *
 * Replace nothing here — this screen exists only as a reference. Delete the file
 * + [dev.shipkaro.kit.core.navigation.Route.ComponentsCatalog] +
 * the Home button if you don't want it in your shipped app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentsCatalogScreen(onBack: () -> Unit) {
    val snackbar = rememberKitSnackbarController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.catalog_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            KitTheme.icons.back,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { KitSnackbarHost(snackbar.hostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = KitTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg),
        ) {
            CatalogIntro()
            ButtonsSection()
            InputsSection()
            ChipsSection()
            CardsAndListsSection()
            BannersSection()
            AvatarsSection()
            PaywallPiecesSection()
            StateViewsSection()
            SettingsRowsSection()
            DialogsAndSheetsSection()
            SnackbarsSection(snackbar)
            PickersSection()
            ListPatternsSection()
            ProgressAndShimmerSection()
            BadgeAndRatingSection()
            LayoutScaffoldsSection()
            UtilitiesSection()
            OpsSheetsSection()
            Spacer(Modifier.height(KitTheme.spacing.xxl))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Intro
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CatalogIntro() {
    Column(modifier = Modifier.padding(horizontal = KitTheme.spacing.lg)) {
        Text(
            stringResource(R.string.catalog_intro_heading),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(KitTheme.spacing.xs))
        Text(
            stringResource(R.string.catalog_intro_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sections
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ButtonsSection() {
    Section(stringResource(R.string.catalog_section_buttons), "KitButton") {
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitButton(
                text = stringResource(R.string.catalog_button_primary),
                onClick = {},
                style = KitButtonStyle.PRIMARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_button_secondary),
                onClick = {},
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_button_text),
                onClick = {},
                style = KitButtonStyle.TEXT,
            )
            KitButton(
                text = stringResource(R.string.catalog_button_loading),
                onClick = {},
                loading = true,
            )
            KitButton(
                text = stringResource(R.string.catalog_button_with_icon),
                onClick = {},
                icon = KitTheme.icons.add,
            )
            KitButton(
                text = stringResource(R.string.catalog_button_disabled),
                onClick = {},
                enabled = false,
            )
        }
    }
}

@Composable
private fun InputsSection() {
    Section(stringResource(R.string.catalog_section_inputs), "KitTextField · KitPasswordField") {
        var name by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorVal by remember { mutableStateOf("not-an-email") }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.catalog_input_label),
                placeholder = stringResource(R.string.catalog_input_placeholder),
                leadingIcon = KitTheme.icons.account,
                helperText = stringResource(R.string.catalog_input_helper),
            )
            KitPasswordField(value = password, onValueChange = { password = it })
            KitTextField(
                value = errorVal,
                onValueChange = { errorVal = it },
                label = stringResource(R.string.catalog_input_error_label),
                isError = true,
                helperText = stringResource(R.string.catalog_input_error_helper),
            )
        }
    }
}

@Composable
private fun ChipsSection() {
    Section(stringResource(R.string.catalog_section_chips), "KitChip · KitFilterChip") {
        var selectedA by remember { mutableStateOf(true) }
        var selectedB by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
                KitChip(text = stringResource(R.string.catalog_chip_plain), onClick = {})
                KitChip(
                    text = stringResource(R.string.catalog_chip_iconed),
                    onClick = {},
                    leadingIcon = KitTheme.icons.star,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
                KitFilterChip(
                    text = stringResource(R.string.catalog_chip_filter_a),
                    selected = selectedA,
                    onSelectedChange = { selectedA = it },
                )
                KitFilterChip(
                    text = stringResource(R.string.catalog_chip_filter_b),
                    selected = selectedB,
                    onSelectedChange = { selectedB = it },
                )
            }
        }
    }
}

@Composable
private fun CardsAndListsSection() {
    Section(stringResource(R.string.catalog_section_cards), "KitCard · KitListItem") {
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            KitCard {
                Text(
                    stringResource(R.string.catalog_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(KitTheme.spacing.xs))
                Text(
                    stringResource(R.string.catalog_card_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            KitCard(onClick = {}) {
                Text(
                    stringResource(R.string.catalog_card_clickable_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    stringResource(R.string.catalog_card_clickable_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            KitListItem(
                headline = stringResource(R.string.catalog_listitem_headline),
                supporting = stringResource(R.string.catalog_listitem_supporting),
                leading = KitTheme.icons.notification,
                onClick = {},
            )
        }
    }
}

@Composable
private fun BannersSection() {
    Section(stringResource(R.string.catalog_section_banners), "KitBanner · 4 styles") {
        var visible by remember { mutableStateOf(true) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitBanner(
                title = stringResource(R.string.catalog_banner_info_title),
                text = stringResource(R.string.catalog_banner_info_body),
                style = KitBannerStyle.INFO,
            )
            KitBanner(
                title = stringResource(R.string.catalog_banner_success_title),
                text = stringResource(R.string.catalog_banner_success_body),
                style = KitBannerStyle.SUCCESS,
            )
            KitBanner(
                title = stringResource(R.string.catalog_banner_warning_title),
                text = stringResource(R.string.catalog_banner_warning_body),
                style = KitBannerStyle.WARNING,
            )
            KitBanner(
                title = stringResource(R.string.catalog_banner_error_title),
                text = stringResource(R.string.catalog_banner_error_body),
                style = KitBannerStyle.ERROR,
            )
            if (visible) {
                KitBanner(
                    text = stringResource(R.string.catalog_banner_dismissible),
                    onDismiss = { visible = false },
                )
            } else {
                KitButton(
                    text = stringResource(R.string.catalog_banner_restore),
                    onClick = { visible = true },
                    style = KitButtonStyle.TEXT,
                    fillWidth = false,
                )
            }
        }
    }
}

@Composable
private fun AvatarsSection() {
    Section(stringResource(R.string.catalog_section_avatars), "KitAvatar") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg),
        ) {
            KitAvatar(initials = "WK", size = 56.dp)
            KitAvatar(imageUrl = "https://i.pravatar.cc/100", size = 56.dp)
            KitAvatar(size = 56.dp)
        }
    }
}

@Composable
private fun PaywallPiecesSection() {
    Section(stringResource(R.string.catalog_section_paywall), "KitPricingCard · KitFeatureRow · KitBulletRow") {
        var selectedTier by remember { mutableStateOf(1) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
                KitPricingCard(
                    title = stringResource(R.string.catalog_pricing_monthly_title),
                    price = "$4.99",
                    caption = stringResource(R.string.catalog_pricing_monthly_caption),
                    selected = selectedTier == 0,
                    onClick = { selectedTier = 0 },
                    modifier = Modifier.weight(1f),
                )
                KitPricingCard(
                    title = stringResource(R.string.catalog_pricing_yearly_title),
                    price = "$29",
                    caption = stringResource(R.string.catalog_pricing_yearly_caption),
                    badge = stringResource(R.string.catalog_pricing_badge),
                    selected = selectedTier == 1,
                    onClick = { selectedTier = 1 },
                    modifier = Modifier.weight(1f),
                )
                KitPricingCard(
                    title = stringResource(R.string.catalog_pricing_lifetime_title),
                    price = "$79",
                    caption = stringResource(R.string.catalog_pricing_lifetime_caption),
                    selected = selectedTier == 2,
                    onClick = { selectedTier = 2 },
                    modifier = Modifier.weight(1f),
                )
            }
            KitFeatureRow(
                icon = KitTheme.icons.star,
                title = stringResource(R.string.catalog_feature_premium_title),
                description = stringResource(R.string.catalog_feature_premium_body),
            )
            KitFeatureRow(
                icon = KitTheme.icons.shield,
                title = stringResource(R.string.catalog_feature_private_title),
                description = stringResource(R.string.catalog_feature_private_body),
                chipColor = KitTheme.colors.success,
            )
            KitBulletRow(text = stringResource(R.string.catalog_bullet_one))
            KitBulletRow(text = stringResource(R.string.catalog_bullet_two))
            KitBulletRow(text = stringResource(R.string.catalog_bullet_three))
        }
    }
}

@Composable
private fun StateViewsSection() {
    Section(
        stringResource(R.string.catalog_section_states),
        "KitInlineLoading · KitEmptyState · KitErrorState · KitSuccessState",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg)) {
            KitCard {
                KitInlineLoading(label = stringResource(R.string.catalog_state_loading_label))
            }
            KitCard {
                KitEmptyState(
                    title = stringResource(R.string.catalog_state_empty_title),
                    message = stringResource(R.string.catalog_state_empty_body),
                    actionText = stringResource(R.string.catalog_state_empty_action),
                    onAction = {},
                    fullScreen = false,
                )
            }
            KitCard {
                KitErrorState(
                    title = stringResource(R.string.catalog_state_error_title),
                    message = stringResource(R.string.catalog_state_error_body),
                    actionText = stringResource(R.string.catalog_state_error_action),
                    onAction = {},
                    fullScreen = false,
                )
            }
            KitCard {
                KitSuccessState(
                    title = stringResource(R.string.catalog_state_success_title),
                    message = stringResource(R.string.catalog_state_success_body),
                    fullScreen = false,
                )
            }
        }
    }
}

@Composable
private fun SettingsRowsSection() {
    Section(
        stringResource(R.string.catalog_section_settings),
        "SettingsSection · AccountRow · ToggleRow · NavRow · DangerRow · LegalLinks",
    ) {
        var toggle by remember { mutableStateOf(true) }
        Column {
            AccountRow(
                name = stringResource(R.string.catalog_account_name),
                email = "wajahat@shipkaro.dev",
                onClick = {},
            )
            Spacer(Modifier.height(KitTheme.spacing.md))
            SettingsSection(title = stringResource(R.string.catalog_settings_section_general)) {
                ToggleRow(
                    title = stringResource(R.string.catalog_toggle_title),
                    subtitle = stringResource(R.string.catalog_toggle_subtitle),
                    leading = KitTheme.icons.notification,
                    chipColor = KitTheme.colors.info,
                    checked = toggle,
                    onCheckedChange = { toggle = it },
                )
                SettingsDivider()
                NavRow(
                    title = stringResource(R.string.catalog_nav_title),
                    subtitle = stringResource(R.string.catalog_nav_subtitle),
                    leading = KitTheme.icons.language,
                    chipColor = KitTheme.colors.warning,
                    valueText = "English",
                    onClick = {},
                )
                SettingsDivider()
                DangerRow(
                    title = stringResource(R.string.catalog_danger_title),
                    subtitle = stringResource(R.string.catalog_danger_subtitle),
                    leading = KitTheme.icons.delete,
                    onClick = {},
                )
            }
            LegalLinks(
                links = listOf(
                    stringResource(R.string.legal_privacy) to {},
                    stringResource(R.string.legal_terms) to {},
                ),
            )
        }
    }
}

@Composable
private fun DialogsAndSheetsSection() {
    Section(stringResource(R.string.catalog_section_dialogs), "KitDialog · KitBottomSheet") {
        var dialog by remember { mutableStateOf(false) }
        var destructiveDialog by remember { mutableStateOf(false) }
        var sheet by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitButton(
                text = stringResource(R.string.catalog_action_show_dialog),
                onClick = { dialog = true },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_action_show_destructive_dialog),
                onClick = { destructiveDialog = true },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_action_show_sheet),
                onClick = { sheet = true },
                style = KitButtonStyle.SECONDARY,
            )
        }
        if (dialog) {
            KitDialog(
                title = stringResource(R.string.catalog_dialog_title),
                message = stringResource(R.string.catalog_dialog_body),
                confirmLabel = stringResource(R.string.catalog_dialog_confirm),
                dismissLabel = stringResource(R.string.catalog_dialog_dismiss),
                onConfirm = { dialog = false },
                onDismiss = { dialog = false },
            )
        }
        if (destructiveDialog) {
            KitDialog(
                title = stringResource(R.string.catalog_dialog_destructive_title),
                message = stringResource(R.string.catalog_dialog_destructive_body),
                confirmLabel = stringResource(R.string.catalog_dialog_destructive_confirm),
                dismissLabel = stringResource(R.string.catalog_dialog_dismiss),
                onConfirm = { destructiveDialog = false },
                onDismiss = { destructiveDialog = false },
                destructive = true,
            )
        }
        if (sheet) {
            KitBottomSheet(onDismiss = { sheet = false }) {
                Text(
                    stringResource(R.string.catalog_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(KitTheme.spacing.sm))
                Text(
                    stringResource(R.string.catalog_sheet_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(KitTheme.spacing.lg))
                KitButton(
                    text = stringResource(R.string.catalog_sheet_dismiss),
                    onClick = { sheet = false },
                )
                Spacer(Modifier.height(KitTheme.spacing.md))
            }
        }
    }
}

@Composable
private fun SnackbarsSection(
    snackbar: dev.shipkaro.kit.core.designsystem.components.KitSnackbarController,
) {
    val infoMessage = stringResource(R.string.catalog_snackbar_info_message)
    val infoTitle = stringResource(R.string.catalog_snackbar_info_title)
    val successMessage = stringResource(R.string.catalog_snackbar_success_message)
    val warningMessage = stringResource(R.string.catalog_snackbar_warning_message)
    val warningAction = stringResource(R.string.catalog_snackbar_warning_action)
    val errorMessage = stringResource(R.string.catalog_snackbar_error_message)
    val errorTitle = stringResource(R.string.catalog_snackbar_error_title)
    val errorAction = stringResource(R.string.catalog_snackbar_error_action)
    Section(stringResource(R.string.catalog_section_snackbars), "KitSnackbar · 4 styles · INFO / SUCCESS / WARNING / ERROR") {
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitButton(
                text = stringResource(R.string.catalog_snackbar_show_info),
                onClick = { snackbar.info(message = infoMessage, title = infoTitle) },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_snackbar_show_success),
                onClick = { snackbar.success(message = successMessage) },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_snackbar_show_warning),
                onClick = {
                    snackbar.warning(
                        message = warningMessage,
                        actionLabel = warningAction,
                        onAction = {},
                    )
                },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_snackbar_show_error),
                onClick = {
                    snackbar.error(
                        title = errorTitle,
                        message = errorMessage,
                        actionLabel = errorAction,
                        onAction = {},
                    )
                },
                style = KitButtonStyle.SECONDARY,
            )
        }
    }
}

@Composable
private fun PickersSection() {
    Section(
        stringResource(R.string.catalog_section_pickers),
        "KitSlider · KitStepper · KitSegmentedButton · KitDropdownMenu · KitSearchField · KitOtpField · KitDatePicker",
    ) {
        var slider by remember { mutableStateOf(0.6f) }
        var steps by remember { mutableStateOf(2) }
        var segIndex by remember { mutableStateOf(0) }
        var dropdown by remember { mutableStateOf("Daily") }
        var search by remember { mutableStateOf("") }
        var otp by remember { mutableStateOf("") }
        var dateVisible by remember { mutableStateOf(false) }
        var pickedDate by remember { mutableStateOf<Long?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            KitSlider(
                label = stringResource(R.string.catalog_slider_label),
                value = slider,
                onValueChange = { slider = it },
            )
            KitStepper(
                label = stringResource(R.string.catalog_stepper_label),
                value = steps,
                onValueChange = { steps = it },
                valueRange = 0..10,
            )
            KitSegmentedButton(
                options = listOf(
                    stringResource(R.string.catalog_segmented_a),
                    stringResource(R.string.catalog_segmented_b),
                    stringResource(R.string.catalog_segmented_c),
                ),
                selectedIndex = segIndex,
                onSelectedChange = { segIndex = it },
            )
            KitDropdownMenu(
                label = stringResource(R.string.catalog_dropdown_label),
                selected = dropdown,
                options = listOf("Daily", "Weekly", "Monthly", "Yearly"),
                onSelectedChange = { dropdown = it },
            )
            KitSearchField(
                value = search,
                onValueChange = { search = it },
                placeholder = stringResource(R.string.catalog_search_placeholder),
            )
            Text(
                stringResource(R.string.catalog_otp_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            KitOtpField(value = otp, onValueChange = { otp = it }, length = 6)
            KitButton(
                text = stringResource(R.string.catalog_datepicker_show),
                onClick = { dateVisible = true },
                style = KitButtonStyle.SECONDARY,
            )
            if (pickedDate != null) {
                Text(
                    text = stringResource(R.string.catalog_datepicker_selected, pickedDate ?: 0L),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            KitDatePicker(
                visible = dateVisible,
                onDismiss = { dateVisible = false },
                onDateSelected = { pickedDate = it },
            )
        }
    }
}

@Composable
private fun ListPatternsSection() {
    Section(
        stringResource(R.string.catalog_section_list_patterns),
        "KitSwipeableRow · KitDisclosureGroup",
    ) {
        var swipedAway by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            if (!swipedAway) {
                KitSwipeableRow(
                    onSwipeEndToStart = { swipedAway = true },
                    onSwipeStartToEnd = { swipedAway = true },
                ) {
                    KitListItem(
                        headline = stringResource(R.string.catalog_swipe_row_title),
                        supporting = stringResource(R.string.catalog_swipe_row_hint),
                        leading = KitTheme.icons.notification,
                    )
                }
            } else {
                KitButton(
                    text = stringResource(R.string.catalog_swipe_restore),
                    onClick = { swipedAway = false },
                    style = KitButtonStyle.TEXT,
                    fillWidth = false,
                )
            }
            KitDisclosureGroup(
                title = stringResource(R.string.catalog_disclosure_title),
                subtitle = stringResource(R.string.catalog_disclosure_subtitle),
                leading = KitTheme.icons.info,
            ) {
                Text(
                    stringResource(R.string.catalog_disclosure_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProgressAndShimmerSection() {
    Section(
        stringResource(R.string.catalog_section_progress),
        "KitLinearProgress · KitIndeterminateLinearProgress · KitSkeleton (shimmer)",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            KitLinearProgress(
                progress = 0.42f,
                label = stringResource(R.string.catalog_linear_label),
            )
            KitIndeterminateLinearProgress()
            KitCard {
                KitSkeletonListItem()
            }
        }
    }
}

@Composable
private fun BadgeAndRatingSection() {
    Section(
        stringResource(R.string.catalog_section_badge_rating),
        "KitBadge · KitRatingBar",
    ) {
        var rating by remember { mutableStateOf(4) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KitBadge(count = 3) {
                    Icon(KitTheme.icons.notification, contentDescription = null)
                }
                KitBadge(count = 142) {
                    Icon(KitTheme.icons.email, contentDescription = null)
                }
                KitBadge(text = "NEW") {
                    Icon(KitTheme.icons.star, contentDescription = null)
                }
                KitBadge {
                    Icon(KitTheme.icons.shield, contentDescription = null)
                }
            }
            KitRatingBar(rating = rating, onRatingChange = { rating = it })
        }
    }
}

@Composable
private fun LayoutScaffoldsSection() {
    Section(
        stringResource(R.string.catalog_section_scaffolds),
        "KitFab · KitBottomNavBar",
    ) {
        var navIndex by remember { mutableStateOf(0) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg)) {
                KitFab(
                    onClick = {},
                    icon = KitTheme.icons.add,
                    contentDescription = stringResource(R.string.catalog_fab_desc),
                )
                KitFab(
                    onClick = {},
                    icon = KitTheme.icons.edit,
                    label = stringResource(R.string.catalog_fab_extended_label),
                )
            }
            KitBottomNavBar(
                items = listOf(
                    KitBottomNavItem(
                        label = stringResource(R.string.catalog_bottomnav_home),
                        icon = KitTheme.icons.account,
                        onClick = { navIndex = 0 },
                    ),
                    KitBottomNavItem(
                        label = stringResource(R.string.catalog_bottomnav_search),
                        icon = KitTheme.icons.search,
                        onClick = { navIndex = 1 },
                    ),
                    KitBottomNavItem(
                        label = stringResource(R.string.catalog_bottomnav_inbox),
                        icon = KitTheme.icons.email,
                        badge = "5",
                        onClick = { navIndex = 2 },
                    ),
                    KitBottomNavItem(
                        label = stringResource(R.string.catalog_bottomnav_settings),
                        icon = KitTheme.icons.settings,
                        onClick = { navIndex = 3 },
                    ),
                ),
                selectedIndex = navIndex,
            )
        }
    }
}

@Composable
private fun UtilitiesSection() {
    Section(
        stringResource(R.string.catalog_section_utilities),
        "EmailLauncher · rememberKitHaptics · KitWebView",
    ) {
        val context = LocalContext.current
        val haptics = rememberKitHaptics()
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitButton(
                text = stringResource(R.string.catalog_email_send),
                onClick = {
                    EmailLauncher.send(
                        context = context,
                        to = "hello@shipkaro.dev",
                        subject = "Kit feedback",
                        body = "I love...",
                    )
                },
                style = KitButtonStyle.SECONDARY,
                icon = KitTheme.icons.email,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
                KitButton(
                    text = stringResource(R.string.catalog_haptic_light),
                    onClick = { haptics.lightTap() },
                    style = KitButtonStyle.SECONDARY,
                    fillWidth = false,
                )
                KitButton(
                    text = stringResource(R.string.catalog_haptic_success),
                    onClick = { haptics.success() },
                    style = KitButtonStyle.SECONDARY,
                    fillWidth = false,
                )
            }
            Text(
                stringResource(R.string.catalog_webview_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OpsSheetsSection() {
    Section(stringResource(R.string.catalog_section_ops), "KitUpdateSheet · KitWhatsNewSheet") {
        var requiredUpdate by remember { mutableStateOf(false) }
        var optionalUpdate by remember { mutableStateOf(false) }
        var whatsNew by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm)) {
            KitButton(
                text = stringResource(R.string.catalog_action_show_required_update),
                onClick = { requiredUpdate = true },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_action_show_optional_update),
                onClick = { optionalUpdate = true },
                style = KitButtonStyle.SECONDARY,
            )
            KitButton(
                text = stringResource(R.string.catalog_action_show_whats_new),
                onClick = { whatsNew = true },
                style = KitButtonStyle.SECONDARY,
            )
        }
        val changes = listOf(
            stringResource(R.string.catalog_whatsnew_one),
            stringResource(R.string.catalog_whatsnew_two),
            stringResource(R.string.catalog_whatsnew_three),
        )
        if (requiredUpdate) {
            KitUpdateSheet(
                title = stringResource(R.string.update_required_title),
                message = stringResource(R.string.update_required_message),
                updateLabel = stringResource(R.string.update_action),
                laterLabel = stringResource(R.string.update_later),
                onUpdate = { requiredUpdate = false },
                onLater = { requiredUpdate = false },
                dismissible = false,
            )
        }
        if (optionalUpdate) {
            KitUpdateSheet(
                title = stringResource(R.string.update_optional_title),
                message = stringResource(R.string.update_optional_message),
                updateLabel = stringResource(R.string.update_action),
                laterLabel = stringResource(R.string.update_later),
                onUpdate = { optionalUpdate = false },
                onLater = { optionalUpdate = false },
                dismissible = true,
                changes = changes,
                changesLabel = stringResource(R.string.catalog_whatsnew_section),
            )
        }
        if (whatsNew) {
            KitWhatsNewSheet(
                title = stringResource(R.string.catalog_whatsnew_title),
                versionLabel = stringResource(R.string.catalog_whatsnew_version, "0.2.0"),
                changes = changes,
                dismissLabel = stringResource(R.string.catalog_sheet_dismiss),
                onDismiss = { whatsNew = false },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun Section(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KitTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(KitTheme.spacing.xs))
        content()
    }
}
