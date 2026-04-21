// tool functions

function getRandomSubarray(arr, n) {
    // Mélange le tableau
    const shuffled = [...arr].sort(() => 0.5 - Math.random());
    // Retourne les n premiers éléments
    return shuffled.slice(0, n);
}
