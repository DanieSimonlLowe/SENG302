var blockSize = 25;
var total_row = 17;
var total_col = 17;
var board;
var context;

var snakeX = blockSize * 5;
var snakeY = blockSize * 5;
var speedX = 0;
var speedY = 0;
var snakeBody = [];
var foodX;
var foodY;
var gameOver = false;

let currentScore = 0;
let highScore = 0;

document.addEventListener("keydown", function (e) {
    if (e.key === "ArrowUp" || e.key === "ArrowDown") {
        e.preventDefault();
    }
});

window.onload = function () {
    board = document.getElementById("board");
    board.height = total_row * blockSize;
    board.width = total_col * blockSize;
    context = board.getContext("2d");

    placeFood();
    document.addEventListener("keyup", changeDirection);
    document.addEventListener("keydown", restartGame);
    setInterval(update, 1000 / 10);
}

function update() {
    if (gameOver) {
        endGame();
        return;
    }

    //Colour of the board and size of the board
    context.fillStyle = "green";
    context.fillRect(0, 0, board.width, board.height);

    //Colour of the food and size of the food
    context.fillStyle = "yellow";
    context.fillRect(foodX, foodY, blockSize, blockSize);

    if (snakeX == foodX && snakeY == foodY) {
        snakeBody.push([foodX, foodY]);
        placeFood();
        eatFood();
    }

    for (let i = snakeBody.length - 1; i > 0; i--) {
        snakeBody[i] = snakeBody[i - 1];
    }
    if (snakeBody.length) {
        snakeBody[0] = [snakeX, snakeY];
    }

    //Colour of the snake and size of the snake
    context.fillStyle = "white";
    snakeX += speedX * blockSize;
    snakeY += speedY * blockSize;
    context.fillRect(snakeX, snakeY, blockSize, blockSize);
    for (let i = 0; i < snakeBody.length; i++) {
        context.fillRect(snakeBody[i][0], snakeBody[i][1], blockSize, blockSize);
    }

    if (snakeX < 0 || snakeX > total_col * blockSize || snakeY < 0 || snakeY > total_row * blockSize) {
        gameOver = true;
    }

    for (let i = 0; i < snakeBody.length; i++) {
        if (snakeX == snakeBody[i][0] && snakeY == snakeBody[i][1]) {
            gameOver = true;
        }
    }

}

function eatFood() {
    currentScore += 10; // Add 10 points for eating food
    updateScoreDisplay();
}

function updateScoreDisplay() {
    document.getElementById('score').innerText = `Score: ${currentScore}`;
}

function endGame() {
    if (currentScore > highScore) {
        highScore = currentScore;
        document.getElementById('highScore').innerText = `High Score: ${highScore}`;
    }
    resetScore();
    showGameOverScreen();
}

function resetScore() {
    currentScore = 0;
    updateScoreDisplay();
}

// Change the direction of the snake
function changeDirection(e) {
    //Stop the up and down arrows from scrolling the page
    e.preventDefault();

    if (e.code == "ArrowUp" && speedY != 1) {
        speedX = 0;
        speedY = -1;
    } else if (e.code == "ArrowDown" && speedY != -1) {
        speedX = 0;
        speedY = 1;
    } else if (e.code == "ArrowLeft" && speedX != 1) {
        speedX = -1;
        speedY = 0;
    } else if (e.code == "ArrowRight" && speedX != -1) {
        speedX = 1;
        speedY = 0;
    }
}


// Show the game over screen
function showGameOverScreen() {
    gameOver = true;
    const gameOverScreen = document.getElementById("gameOverScreen");
    const gameContainer = document.getElementById("board");

    // Center the "Game Over" screen within the game container
    const centerX = gameContainer.clientWidth / 2;
    const centerY = gameContainer.clientHeight / 2;

    // Set the position of the "Game Over" screen
    gameOverScreen.style.position = "relative";
    gameOverScreen.style.top = "50%";
    gameOverScreen.style.left = "50%";
    gameOverScreen.style.transform = "translate(-50%, -101.5%)";

    // Set the dimensions
    gameOverScreen.style.width = `${gameContainer.clientWidth}px`;
    gameOverScreen.style.height = `${gameContainer.clientHeight}px`;

    gameOverScreen.classList.remove("hidden");
}

function restartGame(e) {
    if (e.key === "r" || e.key === "R") {
        document.getElementById("gameOverScreen").classList.add("hidden");
        gameOver = false;
        snakeX = blockSize * 5;
        snakeY = blockSize * 5;
        speedX = 0;
        speedY = 0;
        snakeBody = [];
        placeFood();
    }
}

function placeFood() {
    foodX = Math.floor(Math.random() * total_col) * blockSize;
    foodY = Math.floor(Math.random() * total_row) * blockSize;
}