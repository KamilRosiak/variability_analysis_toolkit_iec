package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

public class MutationParameters {

	/**
	 * Enum Changer
	 */
	public static final String ENUM_MAX_MUTATIONS = "enumChangerMaxMutations";
	public static final int ENUM_MAX_MUTATIONS_DEFAULT = 1;
	
	/**
	 * Name Changer
	 */
	public static final String NAME_MAX_MUTATIONS = "nameChangerMaxMutations";
	public static final int NAME_MAX_MUTATIONS_DEFAULT = 1;
	
	/**
	 * Number Changer
	 */
	public static final String NUMBER_MAX_MUTATIONS = "numberChangerMaxMutations";
	public static final int NUMBER_MAX_MUTATIONS_DEFAULT = 1;
	public static final String NUMBER_GENERATED_DIGIT_LENGTH = "numberGeneratedDigitLength";
	public static final int NUMBER_GENERATED_DIGIT_LENGTH_DEFAULT = 3;

	
	/**
	 * Statement Inserter
	 */
	public static final String STMT_INS_MAX_MUTATIONS = "stmtInserterMaxMutations";
	public static final int STMT_INS_MAX_MUTATIONS_DEFAULT = 1;

	
	/**
	 * Statement Remover
	 */
	public static final String STMT_REM_MAX_MUTATIONS = "stmtRemoverMaxMutations";
	public static final int STMT_REM_MAX_MUTATIONS_DEFAULT = 1;

	
}
