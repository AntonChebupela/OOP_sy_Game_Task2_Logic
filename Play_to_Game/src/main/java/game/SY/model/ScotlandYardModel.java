package game.SY.model;

import static java.util.Objects.requireNonNull;
import static game.SY.model.Colour.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import game.GameKT.graph.Edge;
import game.GameKT.graph.ImmutableGraph;
import game.GameKT.graph.Node;


import game.GameKT.graph.Graph;

public class ScotlandYardModel implements ScotlandYardGame, Consumer<Move>, MoveVisitor {

	private List<Boolean> rounds;
	private Graph<Integer, Transport> graph;
	private ArrayList<PlayerConfiguration> configurations = new ArrayList<>();
	private List<ScotlandYardPlayer> players = new ArrayList<>();
	private int currentPlayer = 0;
	private int currentRound = NOT_STARTED;
	private Collection<Spectator> spectators = new CopyOnWriteArrayList<>();
	private int lastLocation = 0;


	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
							 PlayerConfiguration mrX, PlayerConfiguration firstDetective,
							 PlayerConfiguration... restOfTheDetectives) {

		this.rounds = requireNonNull(rounds); // проверка на null
		this.graph = requireNonNull(graph);

		if(rounds.isEmpty()) throw new IllegalArgumentException("Empty rounds!");
		if(graph.isEmpty()) throw new IllegalArgumentException("Empty graph!");
		if(mrX.colour != BLACK) throw new IllegalArgumentException("Mr. X should be black!");

		//временной список для проверки на дубликаты
		for(PlayerConfiguration player : restOfTheDetectives)
			configurations.add(requireNonNull(player));
		configurations.add(0, firstDetective);
		configurations.add(0, mrX);


		for(PlayerConfiguration x : configurations){
			players.add(new ScotlandYardPlayer(x.player, x.colour, x.location, x.tickets));
		}

		//дублирующиеся местоположения
		Set<Integer> locationSet = new HashSet<>();
		for(PlayerConfiguration player : configurations){
			if(locationSet.contains(player.location))
				throw new IllegalArgumentException("Duplicate location!");
			locationSet.add(player.location);
		}

		//цвета
		Set<Colour> colourSet = new HashSet<>();
		for(PlayerConfiguration player : configurations){
			if(colourSet.contains(player.colour))
				throw new IllegalArgumentException("Duplicate colour!");
			colourSet.add(player.colour);
		}
		checkTickets();
	}

	//проверка на выдачу билетов
	public void checkTickets(){
		for(PlayerConfiguration p : configurations){
			for(Ticket t : Ticket.values()){
				if(!p.tickets.containsKey(t)) throw new IllegalArgumentException("Player does not have respective tickets!");
			}
			if(p.colour.isDetective()){
				if(p.tickets.get(Ticket.DOUBLE) != 0 || p.tickets.get(Ticket.SECRET) != 0) throw new IllegalArgumentException("Detectives should not contain these tickets!");
			}
		}
	}

	//наблюдатель
	@Override
	public void registerSpectator(Spectator spectator) {
		if(spectators.contains(requireNonNull(spectator)))
			throw new IllegalArgumentException("The spectator is already registered!");
		else spectators.add(spectator);
	}

	//отмена если уже есть
	@Override
	public void unregisterSpectator(Spectator spectator) {
		if(spectators.contains(requireNonNull(spectator)))
			spectators.remove(spectator);
		else throw new IllegalArgumentException("The spectator is not registered!");
	}

	//по цвету
	private ScotlandYardPlayer playerFromColour(Colour colour) {
		for(ScotlandYardPlayer x : players){
			if(colour == x.colour()) return x;
		}
		throw new IllegalArgumentException("colour not found!");
	}

	//не проверяет на допустимость
	public Set<TicketMove> createMoves(Colour colour, int startingPoint) {
		ScotlandYardPlayer p = playerFromColour(colour);
		Set<TicketMove> ticketMoves = new HashSet<>();
		List<Integer> locations = new ArrayList<>();

		for(ScotlandYardPlayer player : players){ // занят ли узел игроком
			if(player.isDetective()) locations.add(player.location());
		}

		Node<Integer> start = new Node<>(startingPoint);
		Collection<Edge<Integer, Transport>> possibilities = graph.getEdgesFrom(start); //все возможные ребра для перемещения для конкретного узла

		for(Edge<Integer, Transport> edge : possibilities){
			// достаточно ли билетов и свободно ли место для перемещения
			if(p.hasTickets(Ticket.fromTransport(edge.data()), 1) && !(locations.contains(edge.destination().value())))
				ticketMoves.add(new TicketMove(colour, Ticket.fromTransport(edge.data()), edge.destination().value()));
			if(p.isMrX() && p.hasTickets(Ticket.SECRET) && !(locations.contains(edge.destination().value())))
				ticketMoves.add(new TicketMove(colour, Ticket.SECRET, edge.destination().value())); // секретный билет для x
		}
		return ticketMoves;
	}
	// множество доступных ходов для цвета в определенный момент
	private Set<Move> validMove(Colour player) {
		ScotlandYardPlayer p = playerFromColour(player);
		Set<Move> validMoves = new HashSet<>();
		int lastRound = rounds.size() - 1;

		if(p.isMrX()){
			Set<TicketMove> singleMove = new HashSet<>(); // множество для хранения если сделан одиночный ход икса
			singleMove.addAll(createMoves(player, p.location())); // тоже самое для играков
			validMoves.addAll(singleMove); //добавления возможных ходов для данного узла

			if(currentRound < lastRound && p.hasTickets(Ticket.DOUBLE)){ //проверка
				for(TicketMove move1 : singleMove){

					Set<TicketMove> doubleMove = new HashSet<>(); //множество для данного узла через который можно двойной ход
					doubleMove.addAll(createMoves(BLACK, move1.destination())); //конечный узел для такого хода

					for(TicketMove move2 : doubleMove){
						if(p.hasTickets(move2.ticket())) validMoves.add(new DoubleMove(BLACK, move1, move2)); // есть ли билет для 2 части хода
						if(move1.ticket() == move2.ticket() && !p.hasTickets(move1.ticket(), 2))validMoves.remove(new DoubleMove(BLACK, move1, move2));
						// проверка что используются одинаковые былеты и удалиние для Блека дабл мува
					}
				}
			}
		}
		if(p.isDetective()) validMoves.addAll(createMoves(player, p.location())); // проверка на детектива и добавление в пул общих ходов
		if(p.isDetective() && validMoves.isEmpty()) validMoves.add(new PassMove(player));

		return Collections.unmodifiableSet(validMoves);
	}


	private boolean roundsAreOver(){
		if(currentRound == rounds.size()  && currentPlayer == 0) return true;
		else return false;
	}


	private boolean mrXIsCaught(){
		ScotlandYardPlayer mrX = playerFromColour(BLACK);
		for(ScotlandYardPlayer p : players){
			if(p.isDetective()){
				if(p.location() == mrX.location()) return true;
			}
		}
		return false;
	}


	private boolean mrXIsStuck(){
		if(validMove(BLACK).isEmpty() && currentPlayer == 0) return true;
		return false;
	}


	private boolean detectivesAreStuck(){
		for(ScotlandYardPlayer p : players){
			if(p.isDetective() && !createMoves(p.colour(), p.location()).isEmpty())
				return false;
		}
		return true;
	}


	@Override
	public void visit(PassMove move){
		moveMade(move);
	}

	// одиночный ход
	@Override
	public void visit(TicketMove move){
		ScotlandYardPlayer p = playerFromColour(move.colour());
		ScotlandYardPlayer mrX = playerFromColour(BLACK);
		p.location(move.destination());
		p.removeTicket(move.ticket()); // удаление билета после использования

		if(p.isDetective()) mrX.addTicket(move.ticket()); //adding the detective's ticket to mrX's pile of tickets

		Move hiddenMove = move; //устанавливаем скрытый код

		if(move.colour().isMrX()) {

			if (!rounds.get(currentRound)) {
				hiddenMove = new TicketMove(BLACK, move.ticket(), lastLocation); //устанавливаем последнее известное расположения икса
			}
			else lastLocation = move.destination(); //обновляем это значения
			startRound(); //обновление информации для зрителей
		}
		moveMade(hiddenMove);
	}

	//Двойной ход
	@Override
	public void visit(DoubleMove move){

		ScotlandYardPlayer mrX = playerFromColour(BLACK);
		TicketMove move1 = move.firstMove();
		TicketMove move2 = move.secondMove();
		mrX.removeTicket(Ticket.DOUBLE); // после хода удаляем билет

		boolean revealRound = rounds.get(currentRound);

		if(!revealRound && !rounds.get(currentRound + 1)){ // проверка этот или следующий раунды - секретны
			moveMade(new DoubleMove(BLACK, move1.ticket(), lastLocation, move2.ticket(), lastLocation)); //если нет то показываем последнее местоположения икса
		}
		else if (revealRound && !rounds.get(currentRound + 1)) {
			moveMade(new DoubleMove(BLACK, move1.ticket(), move1.destination(), move2.ticket(), move1.destination()));
		}
		else if (!revealRound && rounds.get(currentRound + 1)){
			moveMade(new DoubleMove(BLACK, move1.ticket(), lastLocation, move2.ticket(), move2.destination()));
		}
		else {
			moveMade(move); //обновляем инфу для зрителей
		}

		mrX.location(move1.destination()); //когда игра начинает начало и конец совпадают
		mrX.removeTicket(move1.ticket()); //удаление билета после хода

		Move hiddenMove = move1; // 1 ход - скрытый

		if(move.colour().isMrX()) {
			if (!revealRound) { // раскрыт или нет
				hiddenMove = new TicketMove(BLACK, move1.ticket(), lastLocation); //  обновляем скрытый ход до одиночного хода, показывая игрокам и зрителям последнее известное местоположение
			}
			else lastLocation = move1.destination(); // устанавливаем новое положеник икса после 1 хода
		}
		startRound(); //новый
		moveMade(hiddenMove); //для зрителей что ход сделан

		// 2 ход
		mrX.location(move2.destination());
		mrX.removeTicket(move2.ticket());

		hiddenMove = move2;

		if(move.colour().isMrX()) {
			if (!rounds.get(currentRound)) {
				hiddenMove = new TicketMove(BLACK, move2.ticket(), lastLocation);
			}
			else lastLocation = move2.destination();
		}
		startRound();
		moveMade(hiddenMove);
	}
	// доп обработчик кода
	@Override
	public void accept(Move move) {
		currentPlayer = (currentPlayer + 1) % players.size(); // сброс если за границей игроков
		requireNonNull(move);

		if (!validMove(move.colour()).contains(move)) throw new IllegalArgumentException("Incorrect move!");

		move.visit(this);// ход у обьекта

		if(!isGameOver()){
			if(currentPlayer > 0) playMove(); // ходим пока игра есть
			else rotationComplete();
		}
		else returnWinningPlayers();
	}

	private void startRound(){
		currentRound += 1;
		for(Spectator s : spectators) s.onRoundStarted(this, currentRound);
	}

	private void moveMade(Move move){
		for(Spectator s : spectators) s.onMoveMade(this, move);
	}

	private void rotationComplete(){
		for(Spectator s : spectators) s.onRotationComplete(this);
	}

	private void returnWinningPlayers(){
		for(Spectator s : spectators) s.onGameOver(this, getWinningPlayers());
	}

	@Override
	public void startRotate() {

		if(isGameOver()) throw new IllegalStateException("Game is over!");
		else {
			currentPlayer = 0; //cброс игрока если ход завершен
			ScotlandYardPlayer mrX = playerFromColour(BLACK);
			mrX.player().makeMove(this, mrX.location(), validMove(BLACK), this);
		}
	}

	private void playMove(){
		ScotlandYardPlayer p = playerFromColour(getCurrentPlayer());
		p.player().makeMove(this, p.location(), validMove(p.colour()), this);
	}

	@Override
	public Collection<Spectator> getSpectators() {
		return Collections.unmodifiableCollection(spectators);
	}

	@Override
	public List<Colour> getPlayers() {
		List<Colour> colours = new ArrayList<>();
		for(ScotlandYardPlayer player : players) colours.add(player.colour());
		return Collections.unmodifiableList(colours);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		Set<Colour> winner = new HashSet<>();
		List<Colour> allPlayers = getPlayers();

		if(roundsAreOver() || detectivesAreStuck()) winner.add(BLACK);
		else if(mrXIsStuck() || mrXIsCaught()){
			winner.addAll(allPlayers);
			winner.remove(BLACK);
		}
		return Collections.unmodifiableSet(winner);
	}

	@Override
	public Optional<Integer> getPlayerLocation(Colour colour) {
		for(ScotlandYardPlayer player : players) {
			if (colour == player.colour()) {
				if (player.isDetective())
					return Optional.of(player.location());
				else
					return Optional.of(lastLocation);
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Integer> getPlayerTickets(Colour colour, Ticket ticket) {
		for(ScotlandYardPlayer player : players){
			if(colour == player.colour()){
				if(player.tickets().containsKey(ticket))
					return Optional.of(player.tickets().get(ticket));
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean isGameOver() {
		if(roundsAreOver() || mrXIsCaught() || mrXIsStuck() || detectivesAreStuck()) return true;
		else return false;
	}

	@Override
	public Colour getCurrentPlayer() { return getPlayers().get(currentPlayer); }

	@Override
	public int getCurrentRound() { return currentRound;	}

	@Override
	public List<Boolean> getRounds() { return Collections.unmodifiableList(rounds); }

	@Override
	public Graph<Integer, Transport> getGraph() { return new ImmutableGraph<>(graph); }
}