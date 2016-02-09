"use strict";

let body;

const Leaderboard = {
	positions: new Map(),
	countdown: 1 * 60,
	live: false,
	object: Ω(`div.leaderboard`).append(Ω(`div.heading`).append(Ω(`div.position`).withText("Position")).append(Ω(`div.name`).withText("Name")).append(Ω(`div.score`).withText("Score"))).append(`div.entries`),
	animated: false,
	update (timestamp, entries) {
		let position = 0;
		entries.sort((a, b) => b.score - a.score).forEach(entry => entry.position = position ++);
		for (const entry of entries) {
			if (!Leaderboard.positions.has(entry.ID)) {
				Leaderboard.positions.set(entry.ID, {
					position: entry.position,
					name: entry.name,
					score: entry.score,
					object: null
				});
				Leaderboard.addElementForEntry(entry.ID, entry.position);
				Graph.addEntry(entry.ID, entry.name);
				Leaderboard.repositionEntry(entry.ID, entry.position, entries.length, false);
			} else {
				const existingEntry = Leaderboard.positions.get(entry.ID);
				if (existingEntry.position !== entry.position) {
					Leaderboard.repositionEntry(entry.ID, entry.position, entries.length, true);
				}
				if (entry.name !== existingEntry.name) {
					existingEntry.name = entry.name;
					existingEntry.object.querySelector(`.name`).replaceText(entry.name);
				}
				if (entry.score !== existingEntry.score) {
					const difference = entry.score - existingEntry.score;
					existingEntry.score = entry.score;
					existingEntry.object.querySelector(`.score`).replaceText(Leaderboard.formatScore(entry.score));
					const differenceObject = Ω(`span.difference.${difference > 0 ? "gain" : "loss"}`).withText(`${difference > 0 ? "+" : "-"}${Leaderboard.formatScore(Math.abs(difference))}`).withStyle({
						top: existingEntry.object.rect.top + window.scrollY,
						left: existingEntry.object.rect.right - existingEntry.object.querySelector(`.score`).rect.width / 2 + window.scrollX
					}).appendedTo(body);
					window.setTimeout(() => {
						differenceObject.remove();
					}, 1.2 * 1000);
				}
			}
			Graph.addPoint(timestamp, entry.ID, entry.score);
			++ position;
		}
		Graph.draw();
	},
	addElementForEntry (ID, position) {
		const entry = Leaderboard.positions.get(ID);
		entry.object = Ω(`div.entry${Leaderboard.animated ? ".new" : ""}`).append(Ω(`div.position`).withText(entry.position + 1)).append(Ω(`div.name`).withText(entry.name)).append(Ω(`div.score`).withText(Leaderboard.formatScore(entry.score))).appendedTo(Leaderboard.object.querySelector(`.entries`));
		if (Leaderboard.animated) {
			window.setTimeout(() => entry.object.removeClass("new"), 10);
		}
	},
	formatScore (score) {
		const string = `00${score}`;
		return `$${parseInt(`${string.slice(0, -2)}`).toLocaleString()}.${string.slice(-2)}`;
	},
	formatCountdown (seconds) {
		return `${`0${Math.floor(seconds / 60)}`.slice(-2)}:${`0${seconds % 60}`.slice(-2)}`;
	},
	repositionEntry (ID, newPosition, totalPositions, animated) {
		const entry = Leaderboard.positions.get(ID);
		entry.position = newPosition;
		if (Leaderboard.animated && animated) {
			entry.object.addClass("raised");
		}
		entry.object.querySelector(`.position`).replaceText(newPosition + 1);
		entry.object.setStyle({
			top: `calc(30pt * ${newPosition})`,
			zIndex: totalPositions - newPosition
		});
		body.querySelector(`#label-${ID}`).setStyle({
			zIndex: totalPositions - newPosition
		});
		if (Leaderboard.animated && animated) {
			window.setTimeout(() => {
				entry.object.removeClass("raised");
			}, 1.2 * 1000);
		}
	},
	swapEntries (IDA, IDB) {
		const positionA = Leaderboard.positions.get(IDA).position, positionB = Leaderboard.positions.get(IDB).position;
		Leaderboard.repositionEntry(IDA, positionB, Leaderboard.positions.size, true);
		Leaderboard.repositionEntry(IDB, positionA, Leaderboard.positions.size, true);
	}
};

const Graph = {
	object: Ω(`canvas`),
	temporary: Ω(`canvas`),
	histories: new Map(),
	addEntry (ID, name) {
		Graph.histories.set(ID, new Map());
		Ω(`span.identifier.hidden#label-${ID}`).withText(name).appendedTo(body);
	},
	addPoint (timestamp, ID, value) {
		const history = Graph.histories.get(ID);
		if (typeof history !== "undefined") {
			history.set(timestamp, value);
		}
	},
	draw () {
		const canvas = Graph.temporary;
		let context = canvas.getContext("2d");
		const now = performance.now();
		const duration = 1000 * 60;
		const minY = 10000;
		const maxY = 1000000 + minY;
		// Clear the canvas
		context.clearRect(0, 0, canvas.width, canvas.height);

		const strokePath = new Path2D();
		const fillPath = new Path2D();

		// Plot the points
		let totalMin = canvas.width - 1;
		for (const ID of Graph.histories.keys()) {
			const history = Graph.histories.get(ID);
			let first = true;
			let min = null, max = null;
			let currentScore = null;
			for (const pair of history) {
				const x = (1 + (pair[0] - now) / duration) * canvas.width;
				const y = canvas.height * (1 - (pair[1] - minY) / (maxY - minY));
				if (min === null || x < min) {
					min = x;
				}
				if (max === null || x > max) {
					max = x;
					currentScore = (pair[1] - minY) / (maxY - minY);
				}
				[strokePath, fillPath].forEach(path => path[first ? "moveTo" : "lineTo"](x, y));
				if (history.size === 1) {
					strokePath.arc(x, y, 2, 0, Math.PI * 2, false);
				}
				first = false;
			}
			if (min !== null && max !== null) {
				fillPath.lineTo(max, canvas.height);
				fillPath.lineTo(min, canvas.height);
				if (min < totalMin) {
					totalMin = min;
				}
			}
			if (currentScore !== null) {
				const rect = Graph.object.rect;
				body.querySelector(`#label-${ID}`).removeClass("hidden").setStyle({
					top: rect.bottom + window.scrollY - currentScore * rect.height
				});
			}
		}
		let fadeGradient;
		let fadeWidth = 250;
		context.fillStyle = "black";
		context.fill(fillPath);
		context.globalCompositeOperation = "destination-out";
		fadeGradient = context.createLinearGradient(totalMin, 0, totalMin + fadeWidth - 1, 0);
		fadeGradient.addColorStop(0, "rgba(0, 0, 0, 1)");
		fadeGradient.addColorStop(1, "rgba(0, 0, 0, 0)");
		context.fillStyle = fadeGradient;
		context.fillRect(totalMin, 0, 250, canvas.height);
		fadeGradient = context.createLinearGradient(canvas.width - 1 - fadeWidth, 0, canvas.width - 1, 0);
		fadeGradient.addColorStop(0, "rgba(0, 0, 0, 0)");
		fadeGradient.addColorStop(1, "rgba(0, 0, 0, 1)");
		context.fillStyle = fadeGradient;
		context.fillRect(canvas.width - 1 - fadeWidth, 0, fadeWidth, canvas.height);
		context.globalCompositeOperation = "source-over";

		// Fill the area under the lines with a gradient
		let hue = 0;
		const fillGradient = context.createLinearGradient(canvas.width / 2, 0, canvas.width / 2, canvas.height - 1);
		fillGradient.addColorStop(0, `hsla(${hue}, 100%, 50%, 0.7)`);
		fillGradient.addColorStop(1, `hsla(${hue + 240}, 100%, 50%, 0.4)`);
		context.globalCompositeOperation = "source-in";
		context.fillStyle = fillGradient;
		context.fillRect(0, 0, canvas.width, canvas.height);
		context.globalCompositeOperation = "source-over";

		// Draw the lines
		context.strokeStyle = "white";
		context.lineWidth = 4;
		context.shadowBlur = 10;
		context.shadowColor = "white";
		context.stroke(strokePath);

		// Draw the timeline
		context = Graph.object.getContext("2d");
		context.fillStyle = "hsl(0, 0%, 10%)";
		context.fillRect(0, 0, canvas.width, canvas.height);
		const secondsPerDivider = 10;
		const dividers = duration / (1000 * secondsPerDivider) - 1;
		context.beginPath();
		const padding = 20;
		context.textBaseline = "top";
		context.textAlign = "center";
		context.fillStyle = "hsla(0, 0%, 100%, 0.6)";
		context.font = `20pt "Myriad Pro"`;
		for (let i = 0; i < dividers; ++ i) {
			context.moveTo(canvas.width / (dividers + 1) * (i + 1), 0);
			context.lineTo(canvas.width / (dividers + 1) * (i + 1), canvas.height - 1);
			context.fillText(`- ${(dividers - i) * secondsPerDivider}s`, canvas.width / (dividers + 1) * (i + 1), padding);
		}
		context.strokeStyle = "hsla(0, 0%, 100%, 0.2)";
		context.lineWidth = 1;
		context.stroke();
		context.drawImage(canvas.element, 0, 0);
	}
};

window.addEventListener("DOMContentLoaded", () => {
	body = Ω(document.body);

	// Initialise testing data
	let entries = [];
	for (const name of ["Cam", "Ox", "MIT", "Imp"]) {
		entries.push({
			ID: name,
			name: `${name}bot`,
			score: 10000 + 1000000 / 2 + Math.round(Math.random() * 1000000 / 10) - entries.length * 100000
		});
	}
	Leaderboard.update(0, entries);
	Leaderboard.animated = true;

	let progression = 0;
	window.setInterval(() => {
		if (++ progression === 60) {
			const timestamp = performance.now();
			const entries = [];
			const variance = 40000;
			for (const pair of Leaderboard.positions) {
				const entryID = pair[0];
				const entry = pair[1];
				entries.push({
					ID: entryID,
					name: entry.name,
					score: /*10000 + 1000000 / 2 + Math.round(Math.random() * 1000000 / 10) - entries.length * 100000*/ entry.score + Math.random() * variance * 2 - variance
				});
			}
			Leaderboard.update(timestamp, entries);
			progression = 0;
		} else {
			Graph.draw();
		}
	}, 1000 / 60);

	// Initialise the leaderboard
	body.querySelector("#focus").append(Leaderboard.object);
	body.querySelector("#focus").append(Ω(`div#countdown.live`).withText(Leaderboard.formatCountdown(Leaderboard.countdown)));
	window.setInterval(() => {
		if (-- Leaderboard.countdown > 0) {
			if (body.querySelector(`#countdown`).hasClass("live")) {
				body.querySelector(`#countdown`).replaceText(Leaderboard.formatCountdown(Leaderboard.countdown));
			} else {
				body.querySelector(`#countdown > span`).replaceText(`Next round in: ${Leaderboard.formatCountdown(Leaderboard.countdown)}`);
			}
		} else {
			Leaderboard.countdown = 60;
			if (body.querySelector(`#countdown`).hasClass("live")) {
				body.querySelector(`#countdown`).removeClass("live").replaceText("Round Complete").append(Ω(`span`).withText(`Next round in: ${Leaderboard.formatCountdown(Leaderboard.countdown)}`));
			} else {
				body.querySelector(`#countdown`).addClass("live").replaceText(Leaderboard.formatCountdown(Leaderboard.countdown));
			}
		}
	}, 1000);
	
	// Initialise the graph
	let width = Leaderboard.object.rect.width;
	let height = 320;
	for (const object of [Graph.object, Graph.temporary]) {
		object.width = width * 2;
		object.height = height * 2;
		object.setStyle({ width, height });
	}
	Graph.draw();
	body.querySelector("#focus").insert(Graph.object);
	body.listenFor("dblclick", () => {
		if (document.webkitFullscreenElement === null) {
			body.element.webkitRequestFullScreen();
		} else {
			document.webkitCancelFullScreen();
		}
	}, false);
});