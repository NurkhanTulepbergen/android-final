package com.example.dms.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dms.R
import com.example.dms.models.*
import com.example.dms.utils.SessionManager
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog

class ResidenceFragment : Fragment() {

    private lateinit var selectBuilding: AutoCompleteTextView
    private lateinit var selectFloor: AutoCompleteTextView
    private lateinit var selectRoom: AutoCompleteTextView
    private lateinit var checkInButton: MaterialButton
    private lateinit var sessionManager: SessionManager

    private val buildings = mutableListOf<Building>()
    private val floors = mutableListOf<Floor>()
    private val rooms = mutableListOf<Room>()
    private var selectedBuilding: Building? = null
    private var selectedFloor: Floor? = null
    private var selectedRoom: Room? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_residence, container, false)

        sessionManager = SessionManager(requireContext())
        selectBuilding = view.findViewById(R.id.selectBuilding)
        selectFloor = view.findViewById(R.id.selectFloor)
        selectRoom = view.findViewById(R.id.selectRoom)
        checkInButton = view.findViewById(R.id.checkInButton)

        // Изначально кнопка неактивна
        checkInButton.isEnabled = false
        checkInButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.grey)

        loadBuildings()
        setupFloorClick()
        setupRoomClick()
        setupTextWatchers()

        checkInButton.setOnClickListener {
            if (selectedRoom == null) {
                Toast.makeText(requireContext(), "Пожалуйста, выберите все поля", Toast.LENGTH_SHORT).show()
            } else {
                val buildingName = selectedBuilding?.address ?: "Неизвестно"
                val floorNum = selectedFloor?.floorNumber ?: 0
                val roomNum = selectedRoom?.roomNumber ?: ""
                showCheckInDialog(buildingName, floorNum.toString(), roomNum)
            }
        }

        return view
    }

    private fun loadBuildings() {
        val response = listOf(
            Building(1, "Корпус А", 5),
            Building(2, "Корпус Б", 4),
            Building(3, "Корпус В", 6)
        )

        buildings.clear()
        buildings.addAll(response)
        setupBuildingDropdown()
    }

    private fun setupBuildingDropdown() {
        val buildingNames = buildings.map { it.address }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, buildingNames)
        selectBuilding.setAdapter(adapter)

        selectBuilding.setOnClickListener { selectBuilding.showDropDown() }

        selectBuilding.setOnItemClickListener { _, _, position, _ ->
            selectedBuilding = buildings[position]
            selectFloor.setText("", false)
            selectRoom.setText("", false)
            selectedFloor = null
            selectedRoom = null
            loadFloors(selectedBuilding!!.id)
        }
    }

    private fun setupFloorClick() { selectFloor.setOnClickListener { selectFloor.showDropDown() } }

    private fun setupRoomClick() { selectRoom.setOnClickListener { selectRoom.showDropDown() } }

    private fun loadFloors(buildingId: Int) {
        val allFloors = listOf(
            Floor(1, 1, 1), Floor(2, 1, 2), Floor(3, 1, 3),
            Floor(4, 2, 1), Floor(5, 2, 2),
            Floor(6, 3, 1), Floor(7, 3, 2), Floor(8, 3, 3)
        )

        floors.clear()
        floors.addAll(allFloors.filter { it.buildingId == buildingId })
        setupFloorDropdown()
    }

    private fun setupFloorDropdown() {
        val floorNumbers = floors.map { it.floorNumber.toString() }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, floorNumbers)
        selectFloor.setAdapter(adapter)

        selectFloor.setOnItemClickListener { _, _, position, _ ->
            selectedFloor = floors[position]
            selectRoom.setText("", false)
            selectedRoom = null
            loadRooms(selectedFloor!!.id)
        }
    }

    private fun loadRooms(floorId: Int) {
        val allRooms = listOf(
            Room(1, 1, "101", 2, 0),
            Room(2, 1, "102", 2, 1),
            Room(3, 2, "201", 2, 0),
            Room(4, 3, "301", 3, 2)
        )

        rooms.clear()
        rooms.addAll(allRooms.filter { it.floorId == floorId })
        setupRoomDropdown()
    }

    private fun setupRoomDropdown() {
        val roomNumbers = rooms.map { it.roomNumber }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, roomNumbers)
        selectRoom.setAdapter(adapter)

        selectRoom.setOnItemClickListener { _, _, position, _ ->
            selectedRoom = rooms[position]
            updateCheckInButtonState()
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateCheckInButtonState() }
            override fun afterTextChanged(s: Editable?) {}
        }

        selectBuilding.addTextChangedListener(watcher)
        selectFloor.addTextChangedListener(watcher)
        selectRoom.addTextChangedListener(watcher)
    }

    private fun updateCheckInButtonState() {
        val isFilled = selectedBuilding != null && selectedFloor != null && selectedRoom != null
        checkInButton.isEnabled = isFilled
        checkInButton.backgroundTintList = if (isFilled)
            ContextCompat.getColorStateList(requireContext(), R.color.purple)
        else
            ContextCompat.getColorStateList(requireContext(), R.color.grey)
    }

    private fun showCheckInDialog(building: String, floor: String, room: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение заселения")
            .setMessage("Вы уверены, что хотите подать запрос на проживание в $building, этаж $floor, комната $room?")
            .setPositiveButton("Да") { dialog, _ ->
                saveRequestLocally(building, floor, room)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun saveRequestLocally(building: String, floor: String, room: String) {
        val sharedPref = requireContext().getSharedPreferences("live_requests", 0)
        val editor = sharedPref.edit()
        val id = System.currentTimeMillis()
        val requestString = "$room|$floor|pending"
        editor.putString(id.toString(), requestString)
        editor.apply()

        Toast.makeText(requireContext(), "Запрос на проживание сохранён локально", Toast.LENGTH_SHORT).show()

        selectBuilding.setText("", false)
        selectFloor.setText("", false)
        selectRoom.setText("", false)
        selectedBuilding = null
        selectedFloor = null
        selectedRoom = null
        updateCheckInButtonState()
    }
}
