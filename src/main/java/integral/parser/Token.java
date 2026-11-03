package integral.parser;

// position: index of the token's first character in the input
public record Token(TokenType type, String text, int position) {}