<div class="statement_back" id="statement_back" style="display:none"></div>
<div class="statement-body">
    <!-- GOAL -->
    <div class="statement-section statement-goal">
        <h1>
            <span class="icon icon-goal">&nbsp;</span>
            <span>The Goal</span>
        </h1>
        <div class="statement-goal-content">
            The aim of this game is to win.
            <br>
            <br>
            For this you'll have to control a submarine to collect plastic waste on corals. Be careful of not drowning !
        </div>
    </div>
    <!-- RULES -->
    <div class="statement-section statement-rules">
        <h1>
            <span class="icon icon-rules">&nbsp;</span>
            <span>Rules</span>
        </h1>
        <div>
            <div class="statement-rules-content">
                The game is played turn by turn.
                <br> 
                <br>
                Your program receives 3 different information:
                <ul>
                    <li>Your current oxygen level</li>
                    <li>The amount of plastic waste at your position</li>
                    <li>The output of the sonar onboard your submarine</li>
                </ul>
                <br>
                In response to these information, you <b>need</b> to output a command: none (equivalent of no command), up, right, down, or left.
                <br>
                <br>
                The game zone works as follows:
                <ul>
                    <li>The central asset of the game is the procedurally generated map in which the players can evolve</li>
                    <li>The map is a 2d tilemap made out of solid blocks/entities (sand walls, submarines, water surface) and hollow blocks (water, corals).</li>
                    <li>The size of the map is not given at the start of the game</li>
                    <li>The map is always symetric</li>
                </ul>
                <br>
                The submarine work as follows:
                <ul>
                    <li>They move around and cannot go through other submarines</li>
                    <li>They automaticly collect plastic waste if there is any at their position</li>
                    <li>They automaticly replenish their oxygen capacity when at the surface</li>
                </ul>
                You <strong>lose</strong> if:
                <ul>
                    <li>You mess up.</li>
                    <li>You do not supply a valid sequence of actions.</li>
                    <li>You drown (oxygen &lt; 0)</li>
                </ul>
            </div>
        </div>
    </div>
    <!-- EXPERT RULES -->
    <div class="statement-section statement-expertrules">
        <h1>
            <span class="icon icon-expertrules">&nbsp;</span>
            <span>Expert Rules</span>
        </h1>
        <div class="statement-expert-rules-content">
            Don't make maps to large as the buffer could be overloaded and generation time could be long.
        </div>
    </div>
    <!-- EXAMPLES -->
    <div class="statement-section statement-examples">
        <h1>
            <span class="icon icon-example">&nbsp;</span>
            <span>Example</span>
        </h1>

        <div class="statement-examples-text">
            A basic situation, an instruction, a result.
        </div>

    </div>
    <!-- WARNING -->
    <div class="statement-section statement-warning">
        <h1>
            <span class="icon icon-warning">&nbsp;</span>
            <span>Note</span>
        </h1>
        <div class="statement-warning-content">
            <b>Don’t forget to run the tests by launching them from the “Test cases” window</b>. You can submit at any
            time
            to recieve a score against the training validators. <b>You can submit as many times as you like</b>. Your
            most
            recent submission will be used for the final ranking.<br>
            <br>
            <strong>Warning:</strong> the validation tests used to compute the final score are not the same as the ones
            used
            during the event.
            Harcoded solutions will not score highly.<br>
            <br>
            Don't hesitate to change the viewer's options to help debug your code (<img
                height="18"
                src="https://www.codingame.com/servlet/fileservlet?id=3463235186409"
                style="opacity:.8;background:#20252a;" width="18">).
        </div>
    </div>
    <!-- PROTOCOL -->
    <div class="statement-section statement-protocol">
        <h1>
            <span class="icon icon-protocol">&nbsp;</span>
            <span>Game Input</span>
        </h1>
        <!-- Protocol block -->
        <div class="blk">
            <div class="text">The program must first read the initialization data from standard input. Then, provide to
                the
                standard output one line with a command (detailed below).
            </div>
        </div>

        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Input</div>
            <div class="text">
                <p><span class="statement-lineno">Line 1: </span>one int <var>oxygen</var>, the current</p>
                <p><span class="statement-lineno">Line 2: </span>one int <var>plasticCount</var>, the amount of plastic at the submarine's position</p>
                <p><span class="statement-lineno"><i>Then 4 lines for the sonar</i></span></p>
                <p><span class="statement-lineno">Line 3: </span>one string <var>y+</var>, formated this way: <i>y+=[WALL | SUBMARINE | CORAL | SURFACE]([0-2⁷]m)</i></p>
                <p><span class="statement-lineno">Line 4: </span>one string <var>x+</var>, formated this way: <i>x+=[WALL | SUBMARINE | CORAL | SURFACE]([0-2⁷]m)</i></p>
                <p><span class="statement-lineno">Line 5: </span>one string <var>y-</var>, formated this way: <i>y-=[WALL | SUBMARINE | CORAL | SURFACE]([0-2⁷]m)</i></p>
                <p><span class="statement-lineno">Line 6: </span>one string <var>x-</var>, formated this way: <i>x-=[WALL | SUBMARINE | CORAL | SURFACE]([0-2⁷]m)</i></p>
            </div>
        </div>

        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Output</div>
            <div class="text">
                <p><span class="statement-lineno">A single line</span> containing one of the following commands (not case sensitive): NONE, UP, RIGHT, DOWN, or LEFT.
                <br><p>
            </div>
        </div>

        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Constraints</div>
            <div class="text">
<const>0</const> ≤ <var>oxygen</var> ≤ <const>42</const><br>
                <const>0</const> ≤ <var>plasticCount</var> ≤ <const>20</const><br>
                <const>13</const> ≤ <var>{y+|x+|y-|x-} </var> ≤ <const>18</const><br>
                <const>0</const> ≤ <var>distance</var> ≤ <const>2⁷</const><br>
                <br>Allotted response time to output
                is ≤
                <const>2</const>
                seconds.

            </div>
        </div>
    </div>
    <!-- STORY -->
    <div class="statement-story-background">
        <div class="statement-story-cover"
             style="background-size: cover; background-image: url(https://www.codingame.com/servlet/fileservlet?id=2210505809934)">
            <div class="statement-story">
                <h1>Title</h1>
                <div class="story-text">Histoire
                    <br>
                    Totallement optionelle
                </div>
            </div>
        </div>
    </div>
</div>