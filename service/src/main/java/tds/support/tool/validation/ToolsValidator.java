package tds.support.tool.validation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import tds.support.job.ErrorSeverity;
import tds.testpackage.model.TestPackage;
import tds.testpackage.model.Tool;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tds.testpackage.model.Tool.TOOL_DEFAULTS_MAP;

@Component
public class ToolsValidator implements TestPackageValidator {

    private static final Set<String> RECOGNIZED_TOOL_OPTIONS = ImmutableSet.of(
            "TDS_BrailleTrans0",
            "TDS_BrailleTrans1",
            "TDS_PS_L0",
            "TDS_PS_L1",
            "TDS_PS_L2",
            "TDS_PS_L3",
            "TDS_PS_L4",
            "TDS_ITTC0",
            "TDS_ITTC_Pitch",
            "TDS_ITTC_Rate",
            "TDS_ITTC_Volume",
            "TDS_ASL0",
            "TDS_ASL1",
            "TDS_APC_PSP",
            "TDS_APC_SCRUBBER",
            "TDS_BT0",
            "TDS_BT_ECN",
            "TDS_BT_ECT",
            "TDS_BT_EXN",
            "TDS_BT_EXT",
            "TDS_BT_UCN",
            "TDS_BT_UCT",
            "TDS_BT_UXN",
            "TDS_BT_UXT",
            "TDS_ClosedCap0",
            "TDS_ClosedCap1",
            "TDS_CC0",
            "TDS_CCInvert",
            "TDS_CCMagenta",
            "TDS_CCMedGrayLtGray",
            "TDS_CCYellowB",
            "TDS_Emboss0",
            "TDS_Emboss_Stim&TDS_Emboss_Item",
            "TDS_ERT0",
            "TDS_ERT_OR",
            "TDS_ERT_OR&TDS_ERT_Auto",
            "TDS_ExpandablePassages0",
            "TDS_ExpandablePassages1",
            "TDS_FT_Serif",
            "TDS_HWPlayback",
            "TDS_Highlight0",
            "TDS_Highlight1",
            "TDS_ILG0",
            "TDS_ILG1",
            "TDS_IF_S14",
            "TDS_ITM1",
            "TDS_MfR0",
            "TDS_MfR1",
            "TDS_Masking0",
            "TDS_Masking1",
            "TDS_Mute0",
            "TDS_Mute1",
            "TDS_Mute2",
            "TDS_Mute3",
            "NEA0",
            "NEA_AR",
            "NEA_NoiseBuf",
            "NEA_RA_Stimuli",
            "NEA_SC_WritItems",
            "NEA_STT",
            "NEDS0",
            "NEDS_CC",
            "NEDS_CO",
            "NEDS_Mag",
            "NEDS_RA_Items",
            "NEDS_SC_Items",
            "NEDS_SS",
            "NEDS_TransDirs",
            "TDS_F_S14",
            "TDS_PM0",
            "TDS_PM1",
            "TDS_PoD0",
            "TDS_PoD_Item",
            "TDS_PoD_Stim",
            "TDS_PoD_Stim&TDS_PoD_Item",
            "TDS_RSL_ListView",
            "TDS_SLM0",
            "TDS_SLM1",
            "TDS_ST0",
            "TDS_ST1",
            "TDS_SC0",
            "TDS_SCNotepad",
            "TDS_SVC1",
            "TDS_TPI_ResponsesFix",
            "TDS_TS_Universal",
            "TDS_TTS0",
            "TDS_TTS_Item",
            "TDS_TTS_Stim",
            "TDS_TTS_Stim&TDS_TTS_Item",
            "TDS_TTSAA0",
            "TDS_TTSAA_Volume&TDS_TTSAA_Pitch&TDS_TTSAA_Rate&TDS_TTSAA_SelectVP",
            "TDS_TTSPause0",
            "TDS_TTSPause1",
            "TDS_TTX_A203",
            "TDS_TTX_A206",
            "TDS_T0",
            "TDS_T1",
            "TDS_WL0",
            "TDS_WL_Glossary",
            "ENU-Braille",
            "TDS_BT_NM",
            "TDS_Calc0",
            "TDS_CalcSciInv",
            "TDS_FT_Verdana",
            "NEA_Abacus",
            "NEA_Calc",
            "NEA_MT",
            "NEDS_RA_Stimuli",
            "NEDS_TArabic",
            "NEDS_TCantonese",
            "NEDS_TFilipino",
            "NEDS_TKorean",
            "NEDS_TMandarin",
            "NEDS_TPunjabi",
            "NEDS_TRussian",
            "NEDS_TSpanish",
            "NEDS_TUkrainian",
            "NEDS_TVietnamese",
            "TDS_WL_ArabicGloss",
            "TDS_WL_ArabicGloss&TDS_WL_Glossary",
            "TDS_WL_CantoneseGloss",
            "TDS_WL_CantoneseGloss&TDS_WL_Glossary",
            "TDS_WL_ESNGlossary",
            "TDS_WL_ESNGlossary&TDS_WL_Glossary",
            "TDS_WL_KoreanGloss",
            "TDS_WL_KoreanGloss&TDS_WL_Glossary",
            "TDS_WL_MandarinGloss",
            "TDS_WL_MandarinGloss&TDS_WL_Glossary",
            "TDS_WL_PunjabiGloss",
            "TDS_WL_PunjabiGloss&TDS_WL_Glossary",
            "TDS_WL_RussianGloss",
            "TDS_WL_RussianGloss&TDS_WL_Glossary",
            "TDS_WL_TagalGloss",
            "TDS_WL_TagalGloss&TDS_WL_Glossary",
            "TDS_WL_UkrainianGloss",
            "TDS_WL_UkrainianGloss&TDS_WL_Glossary",
            "TDS_WL_VietnameseGloss",
            "TDS_WL_VietnameseGloss&TDS_WL_Glossary",
            "TDS_BT_G1",
            "TDS_BT_G2",
            "NEDS_BD",
            "TDS_CalcBasic",
            "TDS_CalcSciInv&TDS_CalcGraphingInv&TDS_CalcRegress",
            "TDS_GN0",
            "TDS_GN1",
            "TDS_Dict0",
            "TDS_Dict_SD4",
            "TDS_DO_ALL",
            "TDS_TH0",
            "TDS_TH_TA",
            "TDS_TO_ALL",
            "TDS_Dict_SD2",
            "TDS_Dict_SD3"
    );

    @Override
    public void validate(final TestPackage testPackage, final List<ValidationError> errors) {
        List<Tool> toolTypes = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.tools().stream())
                .collect(Collectors.toList());

        List<Tool> segmentToolTypes = testPackage.getAssessments().stream()
                .flatMap(assessment -> assessment.getSegments().stream()
                        .flatMap(segment -> segment.tools().stream()))
                .collect(Collectors.toList());

        toolTypes.addAll(segmentToolTypes);

        validateTestToolNamesAreRecognized(errors, toolTypes);
        validateToolARTFieldNamesAreRecognized(errors, toolTypes);
        validateKnownToolCodes(errors, toolTypes);

    }

    private void validateKnownToolCodes(final List<ValidationError> errors, final List<Tool> toolTypes) {
        toolTypes.stream()
                .flatMap(tool -> tool.options().stream()
                        .filter(option -> !RECOGNIZED_TOOL_OPTIONS.contains(option.getCode())))
                .forEach(option -> errors.add(new ValidationError(ErrorSeverity.WARN,
                        String.format("An tool with an unrecognized ISAAP code was detected: %s", option.getCode()))));
    }

    private void validateToolARTFieldNamesAreRecognized(final List<ValidationError> errors, final List<Tool> toolTypes) {
        // Validate that the student package field name matches what we expect (if one is defined)
        toolTypes.stream()
                .filter(tool -> TOOL_DEFAULTS_MAP.containsKey(tool.getName())
                        && !TOOL_DEFAULTS_MAP.get(tool.getName()).getStudentPackageFieldName().equals(tool.getStudentPackageFieldName()))
                .forEach(tool -> errors.add(new ValidationError(ErrorSeverity.WARN,
                        String.format("The tool '%s' contained an unrecognized ART field name '%s'", tool.getName(), tool.getStudentPackageFieldName()))));
    }

    private void validateTestToolNamesAreRecognized(final List<ValidationError> errors, final List<Tool> toolTypes) {
        Set<String> knownToolTypes = TOOL_DEFAULTS_MAP.keySet();

        toolTypes.stream()
                .filter(tool -> !knownToolTypes.contains(tool.getName()))
                .forEach(tool -> errors.add(new ValidationError(ErrorSeverity.WARN,
                        String.format("An unrecognized test tool type with the tool name \"%s\" was detected", tool.getName()))));
    }
}
